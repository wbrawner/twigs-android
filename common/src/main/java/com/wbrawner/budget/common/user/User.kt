package com.wbrawner.budget.common.user

import com.wbrawner.budget.common.Identifiable
import kotlinx.serialization.Serializable

@Serializable
data class User(
    override val id: String? = null,
    val username: String,
    val email: String? = null,
    val avatar: String? = null
) : Identifiable {
    override fun toString(): String = username
}

@Serializable
data class UserPermission(
    val user: String,
    val permission: Permission
)

enum class Permission {
    READ,
    WRITE,
    MANAGE,
    OWNER
}

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)