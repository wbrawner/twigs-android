package com.wbrawner.twigs.shared.user

import com.wbrawner.twigs.shared.Action

sealed interface UserAction: Action {
    data class Login(val username: String, val password: String): UserAction
    data class Register(val username: String, val password: String, val confirmPassword: String): UserAction
    object Logout: UserAction
}

sealed interface UserEffect {

}