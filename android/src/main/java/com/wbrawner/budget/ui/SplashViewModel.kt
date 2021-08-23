package com.wbrawner.budget.ui

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.AsyncViewModel
import com.wbrawner.budget.common.PREF_KEY_TOKEN
import com.wbrawner.budget.common.PREF_KEY_USER_ID
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.user.UserRepository
import com.wbrawner.budget.lib.network.PREF_KEY_BASE_URL
import com.wbrawner.budget.load
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class SplashViewModel : ViewModel(), AsyncViewModel<AuthenticationState> {
    override val state: MutableStateFlow<AsyncState<AuthenticationState>> =
        MutableStateFlow(AsyncState.Loading)

    @Inject
    lateinit var budgetRepository: BudgetRepository

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var userRepository: UserRepository

    suspend fun checkForExistingCredentials() {
        if (!sharedPreferences.contains(PREF_KEY_TOKEN)) {
            state.emit(AsyncState.Success(AuthenticationState.Unauthenticated()))
            return
        }
        state.emit(AsyncState.Success(AuthenticationState.Splash))
        val authState = try {
            userRepository.getProfile()
            AuthenticationState.Authenticated
        } catch (ignored: Exception) {
            AuthenticationState.Unauthenticated()
        }
        state.emit(AsyncState.Success(authState))
    }

    fun login(server: String, username: String, password: String) = load {
        try {
            val correctServer =
                if (server.startsWith("http://") || server.startsWith("https://")) server
                else "https://$server"
            userRepository.login(correctServer, username, password).also {
                sharedPreferences.edit {
                    putString(PREF_KEY_BASE_URL, correctServer)
                    putString(PREF_KEY_USER_ID, it.userId)
                    putString(PREF_KEY_TOKEN, it.token)
                }
                budgetRepository.prefetchData()
            }
            AuthenticationState.Authenticated
        } catch (e: Exception) {
            AuthenticationState.Unauthenticated(server, username, password, e.message)
        }
    }
}

sealed class AuthenticationState {
    object Splash : AuthenticationState()
    class Unauthenticated(
        val server: String? = null,
        val username: String? = null,
        val password: String? = null,
        val errorMessage: String? = null
    ) : AuthenticationState()

    object Authenticated : AuthenticationState()
}