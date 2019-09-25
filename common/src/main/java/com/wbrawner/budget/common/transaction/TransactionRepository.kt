package com.wbrawner.budget.common.transaction

import com.wbrawner.budget.common.Repository

interface TransactionRepository : Repository<Transaction, Long> {
    suspend fun findAll(budgetId: Long? = null, categoryId: Long? = null): Collection<Transaction>
}