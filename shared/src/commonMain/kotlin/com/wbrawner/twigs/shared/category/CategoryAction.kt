package com.wbrawner.twigs.shared.category

import com.wbrawner.twigs.shared.Action
import com.wbrawner.twigs.shared.Reducer
import com.wbrawner.twigs.shared.Route
import com.wbrawner.twigs.shared.State
import com.wbrawner.twigs.shared.budget.BudgetAction
import com.wbrawner.twigs.shared.replace
import kotlinx.coroutines.launch

sealed interface CategoryAction : Action {
    object CategoriesClicked : CategoryAction
    data class BalancesCalculated(
        val budgetBalance: Long,
        val categoryBalances: Map<String, Long>
    ) : CategoryAction

    data class LoadCategoriesSuccess(val categories: List<Category>) : CategoryAction
    data class LoadCategoriesFailed(val error: Exception) : CategoryAction
    object NewCategoryClicked : CategoryAction
    object CancelEditCategory : CategoryAction

    data class CreateCategory(
        val title: String,
        val description: String? = null,
        val amount: Long,
        val expense: Boolean
    ) : CategoryAction

    data class SaveCategorySuccess(val category: Category) : CategoryAction

    data class SaveCategoryFailure(
        val id: String? = null,
        val title: String,
        val description: String? = null,
        val amount: Long,
        val expense: Boolean,
        val error: Exception
    ) : CategoryAction

    data class EditCategory(val id: String) : CategoryAction

    data class SelectCategory(val id: String?) : CategoryAction

    data class UpdateCategory(
        val id: String,
        val title: String,
        val description: String? = null,
        val amount: Long,
        val expense: Boolean,
    ) : CategoryAction

    data class DeleteCategory(val id: String) : CategoryAction
}

class CategoryReducer(private val categoryRepository: CategoryRepository) : Reducer() {
    override fun reduce(action: Action, state: () -> State): State = when (action) {
        is Action.Back -> {
            val currentState = state()
            currentState.copy(
                editingCategory = false,
                selectedCategory = if (currentState.editingCategory) currentState.selectedCategory else null,
                route = if (currentState.route is Route.Categories && !currentState.route.selected.isNullOrBlank() && !currentState.editingCategory) {
                    Route.Categories()
                } else {
                    currentState.route
                }
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

        is CategoryAction.SelectCategory -> state().copy(
            selectedCategory = action.id,
            route = Route.Categories(action.id)
        ).also { newState -> println("Category selected state update: $newState") }

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

        is CategoryAction.CancelEditCategory -> state().copy(editingCategory = false)

        is CategoryAction.NewCategoryClicked -> state().copy(editingCategory = true)

        is CategoryAction.CreateCategory -> {
            val currentState = state()
            val budgetId = requireNotNull(currentState.selectedBudget)
            launch {
                val category = categoryRepository.create(
                    Category(
                        title = action.title,
                        description = action.description,
                        amount = action.amount,
                        budgetId = budgetId
                    )
                )
                dispatch(CategoryAction.SaveCategorySuccess(category))
            }
            currentState.copy(loading = true)
        }

        is CategoryAction.SaveCategorySuccess -> {
            val currentState = state()
            val categories = currentState.categories?.toMutableList() ?: mutableListOf()
            categories.replace(action.category)
            categories.sortBy { it.title }
            currentState.copy(
                loading = false,
                categories = categories.toList(),
                selectedCategory = action.category.id,
                editingCategory = false,
                route = Route.Categories(action.category.id)
            )
        }

        is CategoryAction.EditCategory -> state().copy(
            editingCategory = true,
            selectedCategory = action.id
        )

        is CategoryAction.UpdateCategory -> {
            launch {
                val oldCategory = categoryRepository.findById(action.id)
                val category = categoryRepository.update(
                    oldCategory.copy(
                        title = action.title,
                        description = action.description,
                        amount = action.amount
                    )
                )
                dispatch(CategoryAction.SaveCategorySuccess(category))
            }
            state().copy(loading = true)
        }

        else -> state()
    }
}