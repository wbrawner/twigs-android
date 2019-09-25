package com.wbrawner.budget.auth

import android.content.SharedPreferences

interface CredentialsProvider {
    val username: String
    val password: String
    fun saveCredentials(username: String, password: String)
}

private const val PREF_KEY_USERNAME = "PREF_KEY_USERNAME"
private const val PREF_KEY_PASSWORD = "PREF_KEY_PASSWORD"

class SharedPreferencesCredentialsProvider(private val sharedPreferences: SharedPreferences) : CredentialsProvider {
    override val username: String = sharedPreferences.getString(PREF_KEY_USERNAME, null) ?: ""

    override val password: String = sharedPreferences.getString(PREF_KEY_PASSWORD, null) ?: ""

    override fun saveCredentials(username: String, password: String) {
        sharedPreferences.edit()
                .putString(PREF_KEY_USERNAME, username)
                .putString(PREF_KEY_PASSWORD, password)
                .apply()
    }
}