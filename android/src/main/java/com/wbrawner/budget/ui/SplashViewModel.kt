package com.wbrawner.budget.ui

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wbrawner.budget.*
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.user.UserRepository
import com.wbrawner.budget.lib.network.BaseUrlHelper
import com.wbrawner.budget.lib.network.DEFAULT_URL
import com.wbrawner.budget.lib.network.PREF_KEY_BASE_URL
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
            state.postValue(AuthenticationState.Unauthenticated)
            return
        }
        state.postValue(AsyncState.Success(AuthenticationState.Splash))
        val authState = try {
            userRepository.getProfile()
            AuthenticationState.Authenticated
        } catch (ignored: Exception) {
            AuthenticationState.Unauthenticated
        }
        state.postValue(authState)
    }

    fun login(server: String, username: String, password: String) = launch {
        try {
            val correctServer = if (server.startsWith("http://") || server.startsWith("https://")) server
            else "https://$server"
            baseUrlHelper.url = correctServer
            userRepository.login(username, password).also {
                loadBudgetData()
            }
            sharedPreferences.edit {
                putString(PREF_KEY_BASE_URL, correctServer)
            }
            AuthenticationState.Authenticated
        } catch (ignored: Exception) {
            // TODO: Return error message here
            AuthenticationState.Unauthenticated
        }
    }

    private suspend fun loadBudgetData() {
        budgetRepository.prefetchData()
    }
}

sealed class AuthenticationState {
    object Splash : AuthenticationState()
    object Unauthenticated : AuthenticationState()
    object Authenticated : AuthenticationState()
}