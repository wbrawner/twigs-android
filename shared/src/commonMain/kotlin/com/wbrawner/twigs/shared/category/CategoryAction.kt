package com.wbrawner.twigs.shared.category

import com.wbrawner.twigs.shared.Action
import com.wbrawner.twigs.shared.Reducer
import com.wbrawner.twigs.shared.Route
import com.wbrawner.twigs.shared.State
import com.wbrawner.twigs.shared.budget.BudgetAction
import kotlinx.coroutines.launch

sealed interface CategoryAction : Action {
    object CategoriesClicked : CategoryAction
    data class BalancesCalculated(
        val budgetBalance: Long,
        val categoryBalances: Map<String, Long>
    ) : CategoryAction

    data class LoadCategoriesSuccess(val categories: List<Category>) : CategoryAction
    data class LoadCategoriesFailed(val error: Exception) : CategoryAction
    data class CreateCategory(
        val name: String,
        val description: String? = null,
        val amount: Long,
        val expense: Boolean
    ) : CategoryAction

    data class SaveCategorySuccess(val category: Category) : CategoryAction

    data class SaveCategoryFailure(
        val id: String? = null,
        val name: String,
        val description: String? = null,
        val amount: Long,
        val expense: Boolean,
        val error: Exception
    ) : CategoryAction

    data class EditCategory(val id: String) : CategoryAction

    data class SelectCategory(val id: String?) : CategoryAction

    data class CategorySelected(val id: String) : CategoryAction

    data class UpdateCategory(
        val id: String,
        val name: String,
        val description: String? = null,
        val amount: Long,
        val expense: Boolean
    ) : CategoryAction

    data class DeleteCategory(val id: String) : CategoryAction
}

class CategoryReducer(private val categoryRepository: CategoryRepository) : Reducer() {
    override fun reduce(action: Action, state: () -> State): State = when (action) {
        is Action.Back -> {
            val currentState = state()
            currentState.copy(
                editingCategory = false,
                selectedCategory = if (currentState.editingCategory) currentState.selectedCategory else null
            )
        }

        is CategoryAction.CategoriesClicked -> state().copy(route = Route.Categories())
        is BudgetAction.BudgetSelected -> {
            launch {
                try {
                    val categories = categoryRepository.findAll(budgetIds = arrayOf(action.id))
                    dispatch(CategoryAction.LoadCategoriesSuccess(categories))
                } catch (e: Exception) {
                    dispatch(CategoryAction.LoadCategoriesFailed(e))
                }
            }
            state().copy(categories = null)
        }

        is CategoryAction.LoadCategoriesSuccess -> state().copy(categories = action.categories)
            .also {
                launch {
                    var budgetBalance = 0L
                    val categoryBalances = mutableMapOf<String, Long>()
                    action.categories.forEach { category ->
                        val balance = categoryRepository.getBalance(category.id!!)
                        categoryBalances[category.id] = balance
                        budgetBalance += balance
                    }
                    dispatch(CategoryAction.BalancesCalculated(budgetBalance, categoryBalances))
                }
            }

        is CategoryAction.BalancesCalculated -> state().copy(
            budgetBalance = action.budgetBalance,
            categoryBalances = action.categoryBalances
        )
//        is BudgetAction.CreateBudget -> {
//            launch {
//                val budget = budgetRepository.create(
//                    Budget(
//                        name = action.name,
//                        description = action.description,
//                        users = action.users
//                    )
//                )
//                dispatch(BudgetAction.SaveBudgetSuccess(budget))
//            }
//            state().copy(loading = true)
//        }
//        is BudgetAction.SaveBudgetSuccess -> {
//            val currentState = state()
//            val budgets = currentState.budgets?.toMutableList() ?: mutableListOf()
//            budgets.add(action.budget)
//            budgets.sortBy { it.name }
//            currentState.copy(
//                loading = false,
//                budgets = budgets.toList(),
//                selectedBudget = action.budget.id,
//                editingBudget = false
//            )
//        }
//        is ConfigAction.LoginSuccess -> {
//            launch {
//                try {
//                    val budgets = budgetRepository.findAll()
//                    dispatch(BudgetAction.LoadBudgetsSuccess(budgets))
//                } catch (e: Exception) {
//                    dispatch(BudgetAction.LoadBudgetsFailed(e))
//                }
//            }
//            state().copy(loading = true)
//        }
//        is ConfigAction.Logout -> state().copy(
//            budgets = null,
//            selectedBudget = null,
//            editingBudget = false
//        )
////        is BudgetAction.EditBudget -> state.copy(
////            editingBudget = true,
////            selectedBudget = action.id
////        )
//        is BudgetAction.SelectBudget -> {
//            val currentState = state()
//            val budgetId = currentState.budgets
//                ?.firstOrNull { it.id == action.id }
//                ?.id
//                ?: currentState.budgets?.firstOrNull()?.id
//            settings[KEY_LAST_BUDGET] = budgetId
//            dispatch(BudgetAction.BudgetSelected(budgetId!!))
//            state()
//        }
//        is BudgetAction.BudgetSelected -> state().copy(selectedBudget = action.id)
//
////        is BudgetAction.UpdateBudget -> state.copy(loading = true).also {
////            dispatch(action.async())
////        }
////        is BudgetAction.DeleteBudget -> state.copy(loading = true).also {
////            dispatch(action.async())
////        }
////
////        is BudgetAsyncAction.UpdateBudgetAsync -> {
////            budgetRepository.update(
////                Budget(
////                    id = action.id,
////                    name = action.name,
////                    description = action.description,
////                    users = action.users
////                )
////            )
////            state().copy(
////                loading = false,
////                editingBudget = false,
////            )
////        }
////        is BudgetAsyncAction.DeleteBudgetAsync -> {
////            budgetRepository.delete(action.id)
////            val currentState = state()
////            val budgets = currentState.budgets?.filterNot { it.id == action.id }
////            currentState.copy(
////                loading = false,
////                budgets = budgets,
////                editingBudget = false,
////                selectedBudget = null
////            )
////        }
        else -> state()
    }
}