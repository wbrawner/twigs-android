package com.wbrawner.budget.ui

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wbrawner.budget.common.PREF_KEY_TOKEN
import com.wbrawner.budget.common.PREF_KEY_USER_ID
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.user.UserRepository
import com.wbrawner.budget.lib.network.PREF_KEY_BASE_URL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val budgetRepository: BudgetRepository,
    val userRepository: UserRepository,
    val sharedPreferences: SharedPreferences,
) : ViewModel() {
    val authenticated = MutableStateFlow<Boolean?>(null)
    val loading = MutableStateFlow(false)
    val error = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val server = MutableStateFlow("")
    val username = MutableStateFlow("")
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")
    val enableLogin = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            combine(server, username, password) { (server, username, password) ->
                arrayOf(server.isNotBlank(), username.isNotBlank(), password.isNotBlank())
            }.collect {
                viewModelScope.launch {
                    enableLogin.emit(it.all { it })
                }
            }
        }
    }

    fun checkForExistingCredentials() = viewModelScope.launch {
        if (!sharedPreferences.contains(PREF_KEY_TOKEN)) {
            authenticated.emit(false)
            return@launch
        }
        loading.emit(true)
        try {
            userRepository.getProfile()
            authenticated.emit(true)
        } catch (ignored: Exception) {
            authenticated.emit(false)
        } finally {
            loading.emit(false)
        }
    }

    fun login() = viewModelScope.launch {
        loading.emit(true)
        try {
            val correctServer = with(server.value) {
                if (this.startsWith("http://") || this.startsWith("https://")) this
                else "https://$this"
            }
            userRepository.login(correctServer.trim(), username.value.trim(), password.value.trim())
                .also {
                    sharedPreferences.edit {
                        putString(PREF_KEY_BASE_URL, correctServer)
                        putString(PREF_KEY_USER_ID, it.userId)
                        putString(PREF_KEY_TOKEN, it.token)
                    }
                    budgetRepository.prefetchData()
                    authenticated.emit(true)
                }
        } catch (e: Exception) {
            loading.emit(false)
            error.emit(e.localizedMessage ?: "Login failed")
        }
    }

    fun setServer(server: String) = viewModelScope.launch {
        this@AuthViewModel.server.emit(server)
    }

    fun setUsername(username: String) = viewModelScope.launch {
        this@AuthViewModel.username.emit(username)
    }

    fun setPassword(password: String) = viewModelScope.launch {
        this@AuthViewModel.password.emit(password)
    }
}
