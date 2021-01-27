package com.wbrawner.budget.ui.categories

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.AsyncViewModel
import com.wbrawner.budget.R
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.common.util.randomId
import com.wbrawner.budget.launch
import kotlinx.coroutines.launch
import javax.inject.Inject

class CategoryFormViewModel : ViewModel(), AsyncViewModel<CategoryFormState> {
    override val state: MutableLiveData<AsyncState<CategoryFormState>> = MutableLiveData(AsyncState.Loading)
    @Inject
    lateinit var categoryRepository: CategoryRepository

    @Inject
    lateinit var budgetRepository: BudgetRepository

    fun loadCategory(categoryId: String? = null) {
        launch {
            val category = categoryId?.let {
                categoryRepository.findById(it)
            } ?: Category("", title = "", amount = 0)
            CategoryFormState(
                    category,
                    budgetRepository.findAll().toList()
            )
        }
    }

    fun saveCategory(category: Category) {
        viewModelScope.launch {
            state.postValue(AsyncState.Loading)
            try {
                if (category.id == null)
                    categoryRepository.create(category.copy(id = randomId()))
                else
                    categoryRepository.update(category)
                state.postValue(AsyncState.Exit)
            } catch (e: Exception) {
                state.postValue(AsyncState.Error(e))
            }
        }
    }

    fun deleteCategoryById(id: String) {
        viewModelScope.launch {
            state.postValue(AsyncState.Loading)
            try {
                categoryRepository.delete(id)
                state.postValue(AsyncState.Exit)
            } catch (e: Exception) {
                state.postValue(AsyncState.Error(e))
            }
        }
    }
}

data class CategoryFormState(
        val category: Category,
        val budgets: List<Budget>,
        @StringRes val titleRes: Int,
        val showDeleteButton: Boolean
) {
    constructor(category: Category, budgets: List<Budget>) : this(
            category,
            budgets,
            category.id?.let { R.string.title_edit_category } ?: R.string.title_add_category,
            category.id != null
    )
}