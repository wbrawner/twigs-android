package com.wbrawner.twigs.shared.category

import com.wbrawner.twigs.shared.Repository
import com.wbrawner.twigs.shared.category.Category

interface CategoryRepository : Repository<Category> {
    suspend fun findAll(budgetIds: Array<String>? = null): List<Category>
    suspend fun getBalance(id: String): Long
}