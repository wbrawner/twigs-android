package com.wbrawner.twigs.shared.transaction

import com.wbrawner.twigs.shared.Repository
import com.wbrawner.twigs.shared.endOfMonth
import com.wbrawner.twigs.shared.startOfMonth
import kotlinx.datetime.*

interface TransactionRepository : Repository<Transaction> {
    suspend fun findAll(
            budgetIds: List<String>? = null,
            categoryIds: List<String>? = null,
            start: Instant? = startOfMonth(),
            end: Instant? = endOfMonth()
    ): List<Transaction>
}