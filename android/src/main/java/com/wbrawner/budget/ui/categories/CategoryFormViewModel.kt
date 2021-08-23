package com.wbrawner.budget.ui.categories

import androidx.annotation.StringRes
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
import com.wbrawner.budget.load
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryFormViewModel @Inject constructor(
    val categoryRepository: CategoryRepository,
    val budgetRepository: BudgetRepository
) : ViewModel(), AsyncViewModel<CategoryFormState> {
    override val state: MutableStateFlow<AsyncState<CategoryFormState>> =
        MutableStateFlow(AsyncState.Loading)

    fun loadCategory(categoryId: String? = null) {
        load {
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
            state.emit(AsyncState.Loading)
            try {
                if (category.id == null)
                    categoryRepository.create(category.copy(id = randomId()))
                else
                    categoryRepository.update(category)
                state.emit(AsyncState.Exit)
            } catch (e: Exception) {
                state.emit(AsyncState.Error(e))
            }
        }
    }

    fun deleteCategoryById(id: String) {
        viewModelScope.launch {
            state.emit(AsyncState.Loading)
            try {
                categoryRepository.delete(id)
                state.emit(AsyncState.Exit)
            } catch (e: Exception) {
                state.emit(AsyncState.Error(e))
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