package com.wbrawner.budget.common.transaction

import com.wbrawner.budget.common.Repository
import java.util.*

interface TransactionRepository : Repository<Transaction, String> {
    suspend fun findAll(
            budgetIds: List<String>? = null,
            categoryIds: List<String>? = null,
            start: Calendar? = GregorianCalendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            },
            end: Calendar? = GregorianCalendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                set(Calendar.HOUR_OF_DAY, getActualMaximum(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, getActualMaximum(Calendar.MINUTE))
                set(Calendar.SECOND, getActualMaximum(Calendar.SECOND))
                set(Calendar.MILLISECOND, getActualMaximum(Calendar.MILLISECOND))
            }
    ): List<Transaction>
}