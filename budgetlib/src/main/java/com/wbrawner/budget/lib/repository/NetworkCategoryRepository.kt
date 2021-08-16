package com.wbrawner.budget.lib.repository

import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.lib.network.TwigsApiService
import javax.inject.Inject

class NetworkCategoryRepository @Inject constructor(private val apiService: TwigsApiService) : CategoryRepository {
    override suspend fun create(newItem: Category): Category = apiService.newCategory(newItem)

    override suspend fun findAll(budgetIds: Array<String>?): List<Category> = apiService.getCategories(budgetIds).sortedBy { it.title }

    override suspend fun findAll(): List<Category> = findAll(null)

    override suspend fun findById(id: String): Category = apiService.getCategory(id)

    override suspend fun update(updatedItem: Category): Category =
            apiService.updateCategory(updatedItem.id!!, updatedItem)

    override suspend fun delete(id: String) = apiService.deleteCategory(id)

    override suspend fun getBalance(id: String): Long =
        apiService.sumTransactions(categoryId = id).balance
}
