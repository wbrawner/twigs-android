package com.wbrawner.budget.common.transaction

import com.wbrawner.budget.common.Identifiable
import java.util.*

data class Transaction(
        override val id: Long? = null,
        val title: String,
        val date: Calendar,
        val description: String,
        val amount: Long,
        val categoryId: Long? = null,
        val budgetId: Long,
        val expense: Boolean,
        val createdBy: Long
): Identifiable