package com.wbrawner.budget.common.category

import com.wbrawner.budget.common.Identifiable

data class Category(
        val budgetId: String,
        override val id: String? = null,
        val title: String,
        val description: String? = null,
        val amount: Long,
        val expense: Boolean = true,
        val archived: Boolean = false
): Identifiable {
    override fun toString(): String {
        return title
    }
}
