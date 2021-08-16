package com.wbrawner.budget.common

import java.util.*

const val PREF_KEY_USER_ID = "userId"
const val PREF_KEY_TOKEN = "sessionToken"

data class Session(
        val userId: String,
        val token: String,
        val expiration: Date
)