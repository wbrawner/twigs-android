package com.wbrawner.budget.lib.repository

import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.lib.network.BudgetApiService
import javax.inject.Inject
class NetworkCategoryRepository @Inject constructor(private val apiService: BudgetApiService) : CategoryRepository {
    override suspend fun create(newItem: Category): Category = apiService.newCategory(newItem)

    override suspend fun findAll(accountId: Long?): Collection<Category> = apiService.getCategories(accountId).sortedBy { it.title }

    override suspend fun findAll(): Collection<Category> = findAll(null)

    override suspend fun findById(id: Long): Category = apiService.getCategory(id)

    override suspend fun update(updatedItem: Category): Category =
            apiService.updateCategory(updatedItem.id!!, updatedItem)

    override suspend fun delete(id: Long) = apiService.deleteCategory(id)

    // TODO: Implement this method server-side and then here
    override suspend fun getBalance(id: Long): Long = apiService.getCategoryBalance(id).balance
}

