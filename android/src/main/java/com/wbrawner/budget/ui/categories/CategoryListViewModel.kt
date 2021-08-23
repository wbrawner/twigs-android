package com.wbrawner.budget.ui.categories

import androidx.lifecycle.*
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.AsyncViewModel
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.load
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoryListViewModel : ViewModel(), AsyncViewModel<List<Category>> {
    override val state: MutableStateFlow<AsyncState<List<Category>>> =
        MutableStateFlow(AsyncState.Loading)

    @Inject
    lateinit var budgetRepo: BudgetRepository

    @Inject
    lateinit var categoryRepo: CategoryRepository

    fun getCategories() {
        viewModelScope.launch {
            budgetRepo.currentBudget.collect {
                val budgetId = budgetRepo.currentBudget.replayCache.firstOrNull()?.id
                if (budgetId == null) {
                    state.emit(AsyncState.Error("Invalid budget ID"))
                    return@collect
                }
                load {
                    categoryRepo.findAll(arrayOf(budgetId)).toList()
                }
            }
        }
    }

    suspend fun getBalance(category: Category): Long {
        val multiplier = if (category.expense) -1 else 1
        return categoryRepo.getBalance(category.id!!) * multiplier
    }
}
