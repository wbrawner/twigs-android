package com.wbrawner.budget.common.transaction

import com.wbrawner.budget.common.Identifiable
import java.util.*

data class Transaction(
        override val id: String? = null,
        val title: String,
        val date: Date,
        val description: String? = null,
        val amount: Long,
        val categoryId: String? = null,
        val budgetId: String,
        val expense: Boolean,
        val createdBy: String
): Identifiable