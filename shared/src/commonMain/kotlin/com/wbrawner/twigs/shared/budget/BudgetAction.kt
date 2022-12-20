package com.wbrawner.twigs.shared.budget

import com.wbrawner.twigs.shared.*
import com.wbrawner.twigs.shared.user.UserAction
import com.wbrawner.twigs.shared.user.UserPermission

sealed interface BudgetAction : Action {
    data class CreateBudget(
        val name: String,
        val description: String? = null,
        val users: List<UserPermission> = emptyList()
    ) : BudgetAction {
        fun async() = BudgetAsyncAction.CreateBudgetAsync(name, description, users)
    }

    data class EditBudget(val id: String) : BudgetAction

    data class SelectBudget(val id: String) : BudgetAction

    data class UpdateBudget(
        val id: String,
        val name: String,
        val description: String? = null,
        val users: List<UserPermission> = emptyList()
    ) : BudgetAction {
        fun async() = BudgetAsyncAction.UpdateBudgetAsync(id, name, description, users)
    }

    data class DeleteBudget(val id: String) : BudgetAction {
        fun async() = BudgetAsyncAction.DeleteBudgetAsync(id)
    }
}

sealed interface BudgetAsyncAction : AsyncAction {
    data class CreateBudgetAsync(
        val name: String,
        val description: String? = null,
        val users: List<UserPermission> = emptyList()
    ) : BudgetAsyncAction

    data class UpdateBudgetAsync(
        val id: String,
        val name: String,
        val description: String? = null,
        val users: List<UserPermission> = emptyList()
    ) : BudgetAsyncAction

    data class DeleteBudgetAsync(val id: String) : BudgetAsyncAction
}

class BudgetReducer(private val budgetRepository: BudgetRepository) : AsyncReducer() {
    override fun reduce(action: Action, state: State): State = when (action) {
        is Action.Back -> state.copy(
            editingBudget = false,
            selectedBudget = if (state.editingBudget) state.selectedBudget else null
        )
        is BudgetAction.CreateBudget -> state.copy(loading = true).also {
            dispatch(action.async())
        }
        is BudgetAction.EditBudget -> state.copy(
            editingBudget = true,
            selectedBudget = action.id
        )
        is BudgetAction.SelectBudget -> state.copy(
            selectedBudget = action.id
        )
        is BudgetAction.UpdateBudget -> state.copy(loading = true).also {
            dispatch(action.async())
        }
        is BudgetAction.DeleteBudget -> state.copy(loading = true).also {
            dispatch(action.async())
        }
        is UserAction.Logout -> state.copy(
            editingBudget = false,
            selectedBudget = null,
            budgets = null
        )
        else -> state
    }

    override suspend fun reduce(action: AsyncAction, state: State): State = when (action) {
        is BudgetAsyncAction.CreateBudgetAsync -> {
            val budget = budgetRepository.create(
                Budget(name = action.name, description = action.description, users = action.users)
            )
            val budgets = state.budgets?.toMutableList() ?: mutableListOf()
            budgets.add(budget)
            budgets.sortBy { it.name }
            state.copy(
                loading = false,
                budgets = budgets.toList(),
                selectedBudget = budget.id
            )
        }
        is BudgetAsyncAction.UpdateBudgetAsync -> {
            budgetRepository.update(
                Budget(
                    id = action.id,
                    name = action.name,
                    description = action.description,
                    users = action.users
                )
            )
            state.copy(
                loading = false,
                editingBudget = false,
            )
        }
        is BudgetAsyncAction.DeleteBudgetAsync -> {
            budgetRepository.delete(action.id)
            val budgets = state.budgets?.filterNot { it.id == action.id }
            state.copy(
                loading = false,
                budgets = budgets,
                editingBudget = false,
                selectedBudget = null
            )
        }
        else -> state
    }
}