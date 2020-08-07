package com.wbrawner.budget.ui.categories

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.AsyncViewModel
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.launch
import javax.inject.Inject

class CategoryDetailsViewModel : ViewModel(), AsyncViewModel<CategoryDetails> {
    override val state: MutableLiveData<AsyncState<CategoryDetails>> = MutableLiveData(AsyncState.Loading)

    @Inject
    lateinit var categoryRepo: CategoryRepository

    fun getCategory(id: Long? = null) {
        if (id == null) {
            state.postValue(AsyncState.Error("Invalid category ID"))
            return
        }
        launch {
            val category = categoryRepo.findById(id)
            val multiplier = if (category.expense) -1 else 1
            val balance = categoryRepo.getBalance(category.id!!).toInt() * multiplier
            CategoryDetails(
                    category,
                    balance
            )
        }
    }
}

data class CategoryDetails(
        val category: Category,
        val balance: Int
)