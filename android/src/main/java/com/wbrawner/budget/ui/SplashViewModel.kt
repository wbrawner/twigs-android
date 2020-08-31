package com.wbrawner.budget.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.AsyncViewModel
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.user.UserRepository
import com.wbrawner.budget.launch
import javax.inject.Inject

class SplashViewModel : ViewModel(), AsyncViewModel<AuthenticationState> {
    override val state: MutableLiveData<AsyncState<AuthenticationState>> = MutableLiveData(AsyncState.Loading)

    @Inject
    lateinit var budgetRepository: BudgetRepository

    @Inject
    lateinit var userRepository: UserRepository

    suspend fun checkForExistingCredentials() {
        state.postValue(AsyncState.Success(AuthenticationState.Splash))
        val authState = try {
            AuthenticationState.Authenticated
        } catch (ignored: Exception) {
            AuthenticationState.Unauthenticated
        }
        state.postValue(AsyncState.Success(authState))
    }

    fun login(username: String, password: String) = launch {
        AuthenticationState.Authenticated.also {
            loadBudgetData()
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