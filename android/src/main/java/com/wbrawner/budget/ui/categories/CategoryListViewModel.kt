package com.wbrawner.budget.ui.categories

import androidx.lifecycle.ViewModel
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.category.CategoryRepository
import javax.inject.Inject

class CategoryListViewModel : ViewModel() {
    @Inject lateinit var budgetRepo: BudgetRepository
    @Inject lateinit var categoryRepo: CategoryRepository

    suspend fun getCategory(id: Long): Category = categoryRepo.findById(id)

    suspend fun getCategories(budgetId: Long? = null): Collection<Category> =
            categoryRepo.findAll(budgetId?.let { arrayOf(it) })

    suspend fun saveCategory(category: Category) = if (category.id == null) categoryRepo.create(category)
    else categoryRepo.update(category)

    suspend fun deleteCategoryById(id: Long) = categoryRepo.delete(id)

    suspend fun getBalance(id: Long) = categoryRepo.getBalance(id)
}
