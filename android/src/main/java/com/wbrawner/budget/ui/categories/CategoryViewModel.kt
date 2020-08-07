package com.wbrawner.budget.ui.categories

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.AsyncViewModel
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.common.transaction.TransactionRepository
import javax.inject.Inject

class CategoryViewModel : ViewModel(), AsyncViewModel<List<Category>> {
    override val state: MutableLiveData<AsyncState<List<Category>>> = MutableLiveData(AsyncState.Loading)

    @Inject lateinit var transactionRepo: TransactionRepository
    @Inject lateinit var categoryRepo: CategoryRepository
    @Inject lateinit var budgetRepo: BudgetRepository

    fun getBudget(budgetId: Long): Budget {
        budgetRepo.findById(budgetId)
    }

    suspend fun getBudgets() = showLoader {
        budgetRepo.findAll()
    }

    suspend fun getTransactions(categoryId: Long) = showLoader {
        transactionRepo.findAll(categoryIds = listOf(categoryId))
    }

    suspend fun getCategory(id: Long): Category = showLoader {
        categoryRepo.findById(id)
    }

    suspend fun getCategories(budgetId: Long? = null): Collection<Category> = showLoader {
        categoryRepo.findAll(budgetId?.let { arrayOf(it) })
    }

    suspend fun saveCategory(category: Category) = showLoader {
        if (category.id == null)
            categoryRepo.create(category)
        else
            categoryRepo.update(category)
    }

    suspend fun deleteCategoryById(id: Long) = showLoader {
        categoryRepo.delete(id)
    }

    suspend fun getBalance(category: Category) = showLoader {
        val balance = categoryRepo.getBalance(category.id!!)
        val multiplier = if (category.expense) -1 else 1
        return@showLoader (balance * multiplier).toInt()
    }
}
