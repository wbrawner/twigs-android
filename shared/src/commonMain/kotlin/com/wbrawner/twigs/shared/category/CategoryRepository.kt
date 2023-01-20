package com.wbrawner.twigs.shared.category

import com.wbrawner.twigs.shared.Repository
import com.wbrawner.twigs.shared.network.APIService

interface CategoryRepository : Repository<Category> {
    suspend fun findAll(budgetIds: Array<String>? = null): List<Category>
    suspend fun getBalance(id: String): Long
}

class NetworkCategoryRepository(private val apiService: APIService) : CategoryRepository {
    override suspend fun findAll(budgetIds: Array<String>?): List<Category> =
        apiService.getCategories(budgetIds = budgetIds)

    override suspend fun findAll(): List<Category> = findAll(null)

    override suspend fun getBalance(id: String): Long =
        apiService.sumTransactions(categoryId = id).balance

    override suspend fun create(newItem: Category): Category = apiService.newCategory(newItem)

    override suspend fun findById(id: String): Category = apiService.getCategory(id)

    override suspend fun update(updatedItem: Category): Category =
        apiService.updateCategory(updatedItem.id!!, updatedItem)

    override suspend fun delete(id: String) = apiService.deleteCategory(id)
}