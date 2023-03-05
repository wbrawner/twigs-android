package com.wbrawner.twigs.shared.category

import com.wbrawner.twigs.shared.Action
import com.wbrawner.twigs.shared.Reducer
import com.wbrawner.twigs.shared.Route
import com.wbrawner.twigs.shared.State
import com.wbrawner.twigs.shared.budget.BudgetAction
import com.wbrawner.twigs.shared.replace
import com.wbrawner.twigs.shared.transaction.TransactionAction
import kotlinx.coroutines.launch
import kotlin.math.abs

sealed interface CategoryAction : Action {
    object CategoriesClicked : CategoryAction
    data class BalancesCalculated(
        val categoryBalances: Map<String, Long>,
        val actualIncome: Long,
        val actualExpenses: Long
    ) : CategoryAction

    data class LoadCategoriesSuccess(val categories: List<Category>) : CategoryAction
    data class LoadCategoriesFailed(val error: Exception) : CategoryAction
    object NewCategoryClicked : CategoryAction
    object CancelEditCategory : CategoryAction

    data class CreateCategory(
        val title: String,
        val description: String? = null,
        val amount: Long,
        val expense: Boolean,
        val archived: Boolean,
    ) : CategoryAction

    data class SaveCategorySuccess(val category: Category) : CategoryAction

    data class SaveCategoryFailure(
        val id: String? = null,
        val title: String,
        val description: String? = null,
        val amount: Long,
        val expense: Boolean,
        val archived: Boolean,
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
        val archived: Boolean,
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
            state().copy(
                categories = null,
                selectedCategory = null,
                editingCategory = false,
                categoryBalances = null,
                actualIncome = null,
                actualExpenses = null
            )
        }

        is CategoryAction.SelectCategory -> state().copy(
            selectedCategory = action.id,
            route = Route.Categories(action.id)
        ).also { newState -> println("Category selected state update: $newState") }

        is CategoryAction.LoadCategoriesSuccess -> {
            var expectedIncome = 0L
            var expectedExpenses = 0L
            action.categories.forEach { category ->
                if (category.archived) return@forEach
                if (category.expense) {
                    expectedExpenses += category.amount
                } else {
                    expectedIncome += category.amount
                }
            }
            val currentState = state()
            val defaultCategoryBalances =
                action.categories.associate { it.id!! to 0L }.toMutableMap()
            val categoryBalances: Map<String, Long>? = currentState.categoryBalances?.let {
                defaultCategoryBalances.apply {
                    putAll(it)
                }
            }
            state().copy(
                categories = action.categories,
                categoryBalances = categoryBalances,
                expectedExpenses = expectedExpenses,
                expectedIncome = expectedIncome
            )
        }

        is TransactionAction.LoadTransactionsSuccess -> state()
            .also {
                launch {
                    val categoryBalances =
                        it.categories?.associate { it.id!! to 0L }?.toMutableMap()
                            ?: mutableMapOf()
                    var actualIncome = 0L
                    var actualExpenses = 0L
                    action.transactions.forEach { transaction ->
                        val category = transaction.categoryId
                        var balance = category?.let { categoryBalances[it] ?: 0L } ?: 0L
                        if (transaction.expense) {
                            balance -= transaction.amount
                            actualExpenses += abs(transaction.amount)
                        } else {
                            balance += transaction.amount
                            actualIncome += transaction.amount
                        }
                        category?.let {
                            categoryBalances[it] = balance
                        }
                    }
                    dispatch(
                        CategoryAction.BalancesCalculated(
                            categoryBalances,
                            actualIncome = actualIncome,
                            actualExpenses = actualExpenses
                        )
                    )
                }
            }

        is CategoryAction.BalancesCalculated -> state().copy(
            categoryBalances = action.categoryBalances,
            actualExpenses = action.actualExpenses,
            actualIncome = action.actualIncome
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

enum class CategoryGroup {
    INCOME,
    EXPENSE,
    ARCHIVED
}

val Category.group: CategoryGroup
    get() = when {
        archived -> CategoryGroup.ARCHIVED
        expense -> CategoryGroup.EXPENSE
        else -> CategoryGroup.INCOME
    }

fun List<Category>.groupByType(): Map<CategoryGroup, List<Category>> {
    val groups = mutableMapOf<CategoryGroup, List<Category>>()
    forEach { category ->
        val list = groups[category.group]?.toMutableList() ?: mutableListOf()
        list.add(category)
        groups[category.group] = list.sortedBy { it.title }
    }
    return groups
}