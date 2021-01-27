package com.wbrawner.budget.common

import java.util.*

const val PREF_KEY_TOKEN = "sessionToken"

data class Session(
        val id: String,
        val token: String,
        val expiration: Date
)