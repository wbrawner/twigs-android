package com.wbrawner.budget.ui

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.AsyncViewModel
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.user.UserRepository
import com.wbrawner.budget.launch
import com.wbrawner.budget.lib.network.BaseUrlHelper
import com.wbrawner.budget.lib.network.DEFAULT_URL
import com.wbrawner.budget.lib.network.PREF_KEY_BASE_URL
import com.wbrawner.budget.postValue
import javax.inject.Inject

class SplashViewModel : ViewModel(), AsyncViewModel<AuthenticationState> {
    override val state: MutableLiveData<AsyncState<AuthenticationState>> = MutableLiveData(AsyncState.Loading)
    @Inject
    lateinit var baseUrlHelper: BaseUrlHelper

    @Inject
    lateinit var budgetRepository: BudgetRepository

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var userRepository: UserRepository

    suspend fun checkForExistingCredentials() {
        if (baseUrlHelper.url == DEFAULT_URL) {
            state.postValue(AuthenticationState.Unauthenticated())
            return
        }
        state.postValue(AsyncState.Success(AuthenticationState.Splash))
        val authState = try {
            userRepository.getProfile()
            AuthenticationState.Authenticated
        } catch (ignored: Exception) {
            AuthenticationState.Unauthenticated()
        }
        state.postValue(authState)
    }

    fun login(server: String, username: String, password: String) = launch {
        try {
            val correctServer = if (server.startsWith("http://") || server.startsWith("https://")) server
            else "https://$server"
            baseUrlHelper.url = correctServer
            userRepository.login(username, password).also {
                budgetRepository.prefetchData()
            }
            sharedPreferences.edit {
                putString(PREF_KEY_BASE_URL, correctServer)
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