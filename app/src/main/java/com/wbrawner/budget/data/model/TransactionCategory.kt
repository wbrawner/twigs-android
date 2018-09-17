package com.wbrawner.budget.data.model

class TransactionCategory(
    val id: Int,
    val name: String
) {
    override fun toString(): String {
        return name
    }
}