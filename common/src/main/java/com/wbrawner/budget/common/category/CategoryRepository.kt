package com.wbrawner.budget.common.category

import com.wbrawner.budget.common.Repository

interface CategoryRepository : Repository<Category, Long> {
    suspend fun findAll(budgetIds: Array<Long>? = null): Collection<Category>
    suspend fun getBalance(id: Long): Long
}