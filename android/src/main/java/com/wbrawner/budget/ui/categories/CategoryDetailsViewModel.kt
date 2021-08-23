package com.wbrawner.budget.ui.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.AsyncViewModel
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.load
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryDetailsViewModel @Inject constructor(
    val categoryRepo: CategoryRepository
) : ViewModel(), AsyncViewModel<CategoryDetails> {
    override val state: MutableStateFlow<AsyncState<CategoryDetails>> =
        MutableStateFlow(AsyncState.Loading)

    fun getCategory(id: String? = null) {
        viewModelScope.launch {
            if (id == null) {
                state.emit(AsyncState.Error("Invalid category ID"))
                return@launch
            }
            load {
                val category = categoryRepo.findById(id)
                val multiplier = if (category.expense) -1 else 1
                val balance = categoryRepo.getBalance(id) * multiplier
                CategoryDetails(
                    category,
                    balance,
                    category.amount - balance
                )
            }
        }
    }
}

data class CategoryDetails(
        val category: Category,
        val balance: Long,
        val remaining: Long
)