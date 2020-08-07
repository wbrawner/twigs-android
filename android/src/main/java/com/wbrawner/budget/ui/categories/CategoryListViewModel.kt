package com.wbrawner.budget.ui.categories

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.AsyncViewModel
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.launch
import javax.inject.Inject

class CategoryListViewModel : ViewModel(), AsyncViewModel<List<Category>> {
    override val state: MutableLiveData<AsyncState<List<Category>>> = MutableLiveData(AsyncState.Loading)

    @Inject
    lateinit var budgetRepo: BudgetRepository

    @Inject
    lateinit var categoryRepo: CategoryRepository

    fun getCategories(budgetId: Long? = null) {
        if (budgetId == null) {
            state.postValue(AsyncState.Error("Invalid budget ID"))
            return
        }
        launch {
            categoryRepo.findAll(arrayOf(budgetId)).toList()
        }
    }

    suspend fun getBalance(category: Category): Long {
        val multiplier = if (category.expense) -1 else 1
        return categoryRepo.getBalance(category.id!!) * multiplier
    }
}
