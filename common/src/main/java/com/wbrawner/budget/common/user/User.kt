package com.wbrawner.budget.common.user

data class User(
        val id: Long? = null,
        val username: String,
        val email: String? = null,
        val avatar: String? = null
) {
    override fun toString(): String = username
}

data class LoginRequest(
        val username: String,
        val password: String
)