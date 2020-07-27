package com.wbrawner.budget.common.transaction

import com.wbrawner.budget.common.Repository
import java.util.*

interface TransactionRepository : Repository<Transaction, Long> {
    suspend fun findAll(
            budgetIds: List<Long>? = null,
            categoryIds: List<Long>? = null,
            start: Calendar? = null,
            end: Calendar? = null
    ): Collection<Transaction>
}