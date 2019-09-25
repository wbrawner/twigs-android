package com.wbrawner.budget.common.budget

import com.wbrawner.budget.common.user.User

data class Budget(
        val id: Long? = null,
        val name: String,
        val description: String? = null,
        val users: List<User> = emptyList()
) {
    override fun toString(): String {
        return name
    }
}
