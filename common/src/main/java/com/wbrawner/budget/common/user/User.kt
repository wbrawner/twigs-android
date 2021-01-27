package com.wbrawner.budget.common.user

import com.wbrawner.budget.common.Identifiable

data class User(
        override val id: String? = null,
        val username: String,
        val email: String? = null,
        val avatar: String? = null
) : Identifiable {
    override fun toString(): String = username
}

data class LoginRequest(
        val username: String,
        val password: String
)