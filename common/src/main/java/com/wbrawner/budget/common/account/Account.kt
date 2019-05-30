package com.wbrawner.budget.common.account

data class Account(
        val id: Long? = null,
        val name: String,
        val description: String? = null,
        val currencyCode: String
)