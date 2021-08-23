package com.wbrawner.budget.common

import com.wbrawner.budget.common.transaction.DateSerializer
import kotlinx.serialization.Serializable
import java.util.*

const val PREF_KEY_USER_ID = "userId"
const val PREF_KEY_TOKEN = "sessionToken"

@Serializable
data class Session(
        val userId: String,
        val token: String,
        @Serializable(with = DateSerializer::class)
        val expiration: Date
)