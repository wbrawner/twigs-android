package com.wbrawner.twigs.shared.budget

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import com.wbrawner.twigs.shared.Action
import com.wbrawner.twigs.shared.Reducer
import com.wbrawner.twigs.shared.Route
import com.wbrawner.twigs.shared.State
import com.wbrawner.twigs.shared.replace
import com.wbrawner.twigs.shared.transaction.TransactionAction
import com.wbrawner.twigs.shared.user.ConfigAction
import com.wbrawner.twigs.shared.user.UserPermission
import kotlinx.coroutines.launch

sealed interface BudgetAction : Action {
    object OverviewClicked : BudgetAction
    data class LoadBudgetsSuccess(val budgets: List<Budget>) : BudgetAction
    data class LoadBudgetsFailed(val error: Exception) : BudgetAction
    data class CreateBudget(
        val name: String,
        val description: String? = null,
        val users: List<UserPermission> = emptyList()
    ) : BudgetAction

    data class SaveBudgetSuccess(val budget: Budget) : BudgetAction

    data class SaveBudgetFailure(
        val id: String? = null,
        val name: String,
        val description: String? = null,
        val users: List<UserPermission> = emptyList(),
        val error: Exception
    ) : BudgetAction

    data class EditBudget(val id: String) : BudgetAction

    data class SelectBudget(val id: String?) : BudgetAction

    data class BudgetSelected(val id: String) : BudgetAction

    data class UpdateBudget(
        val id: String,
        val name: String,
        val description: String? = null,
        val users: List<UserPermission> = emptyList()
    ) : BudgetAction

    data class DeleteBudget(val id: String) : BudgetAction
}

const val KEY_LAST_BUDGET = "lastBudget"

class BudgetReducer(
    private val budgetRepository: BudgetRepository,
    private val settings: Settings
) : Reducer() {
    override fun reduce(action: Action, state: () -> State): State = when (action) {
        is Action.Back -> {
            val currentState = state()
            currentState.copy(
                editingBudget = false
            )
        }

        is BudgetAction.OverviewClicked -> state().copy(route = Route.Overview)
        is BudgetAction.LoadBudgetsSuccess -> state().copy(budgets = action.budgets).also {
            dispatch(BudgetAction.SelectBudget(settings.getStringOrNull(KEY_LAST_BUDGET)))
        }

        is BudgetAction.CreateBudget -> {
            launch {
                val budget = budgetRepository.create(
                    Budget(
                        name = action.name,
                        description = action.description,
                        users = action.users
                    )
                )
                dispatch(BudgetAction.SaveBudgetSuccess(budget))
            }
            state().copy(loading = true)
        }

        is BudgetAction.SaveBudgetSuccess -> {
            val currentState = state()
            val budgets = currentState.budgets?.toMutableList() ?: mutableListOf()
            budgets.replace(action.budget)
            budgets.sortBy { it.name }
            currentState.copy(
                loading = false,
                budgets = budgets.toList(),
                selectedBudget = action.budget.id,
                editingBudget = false
            )
        }

        is ConfigAction.LoginSuccess -> {
            launch {
                try {
                    val budgets = budgetRepository.findAll()
                    dispatch(BudgetAction.LoadBudgetsSuccess(budgets))
                } catch (e: Exception) {
                    dispatch(BudgetAction.LoadBudgetsFailed(e))
                }
            }
            state().copy(loading = true)
        }

        is ConfigAction.Logout -> state().copy(
            budgets = null,
            selectedBudget = null,
            editingBudget = false
        )
//        is BudgetAction.EditBudget -> state.copy(
//            editingBudget = true,
//            selectedBudget = action.id
//        )
        is BudgetAction.SelectBudget -> {
            val currentState = state()
            val budgetId = currentState.budgets
                ?.firstOrNull { it.id == action.id }
                ?.id
                ?: currentState.budgets?.firstOrNull()?.id
            settings[KEY_LAST_BUDGET] = budgetId
            dispatch(BudgetAction.BudgetSelected(budgetId!!))
            state()
        }

        is BudgetAction.BudgetSelected -> state().copy(selectedBudget = action.id)

        is TransactionAction.LoadTransactionsSuccess -> {
            val balance = action.transactions.sumOf {
                if (it.expense) {
                    it.amount * -1
                } else {
                    it.amount
                }
            }
            state().copy(budgetBalance = balance)
        }

//        is BudgetAction.UpdateBudget -> state.copy(loading = true).also {
//            dispatch(action.async())
//        }
//        is BudgetAction.DeleteBudget -> state.copy(loading = true).also {
//            dispatch(action.async())
//        }
//
//        is BudgetAsyncAction.UpdateBudgetAsync -> {
//            budgetRepository.update(
//                Budget(
//                    id = action.id,
//                    name = action.name,
//                    description = action.description,
//                    users = action.users
//                )
//            )
//            state().copy(
//                loading = false,
//                editingBudget = false,
//            )
//        }
//        is BudgetAsyncAction.DeleteBudgetAsync -> {
//            budgetRepository.delete(action.id)
//            val currentState = state()
//            val budgets = currentState.budgets?.filterNot { it.id == action.id }
//            currentState.copy(
//                loading = false,
//                budgets = budgets,
//                editingBudget = false,
//                selectedBudget = null
//            )
//        }
        else -> state()
    }
}