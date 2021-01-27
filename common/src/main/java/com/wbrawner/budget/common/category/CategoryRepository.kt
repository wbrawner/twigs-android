package com.wbrawner.budget.common.category

import com.wbrawner.budget.common.Repository

interface CategoryRepository : Repository<Category, String> {
    suspend fun findAll(budgetIds: Array<String>? = null): List<Category>
    suspend fun getBalance(id: String): Long
}