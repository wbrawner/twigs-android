package com.wbrawner.budget.common.category

data class Category(
        val accountId: Long,
        val id: Long? = null,
        val title: String,
        val description: String? = null,
        val amount: Long
) {
    override fun toString(): String {
        return title
    }
}
