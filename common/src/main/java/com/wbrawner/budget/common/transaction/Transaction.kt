package com.wbrawner.budget.common.transaction

import java.util.*

data class Transaction(
        val id: Long? = null,
        val title: String,
        val date: Date,
        val description: String,
        val amount: Long,
        val categoryId: Long? = null,
        val budgetId: Long,
        val expense: Boolean,
        val createdBy: Long
)
