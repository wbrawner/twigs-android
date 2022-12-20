package com.wbrawner.twigs.shared.user

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String? = null,
    val username: String,
    val email: String? = null,
    val avatar: String? = null
)

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

@Serializable
data class Session(
    val userId: String,
    val token: String,
    val expiration: Instant
)