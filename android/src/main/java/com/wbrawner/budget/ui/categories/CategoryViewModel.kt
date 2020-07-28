package com.wbrawner.budget.ui.categories

import androidx.lifecycle.ViewModel
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.common.transaction.TransactionRepository
import com.wbrawner.budget.di.ViewModelKey
import com.wbrawner.budget.ui.base.LoadingViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject

class CategoryViewModel @Inject constructor(
        private val transactionRepo: TransactionRepository,
        private val categoryRepo: CategoryRepository,
        private val budgetRepo: BudgetRepository
) : LoadingViewModel() {
    suspend fun getBudget(budgetId: Long): Budget = showLoader {
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

@Module
abstract class CategoryViewModelMapper {
    @Binds
    @IntoMap
    @ViewModelKey(CategoryViewModel::class)
    abstract fun bindCategoryViewModel(categoryViewModel: CategoryViewModel): ViewModel
}