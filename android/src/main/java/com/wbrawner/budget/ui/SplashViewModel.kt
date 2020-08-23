package com.wbrawner.budget.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.user.User
import com.wbrawner.budget.common.user.UserRepository
import javax.inject.Inject

class SplashViewModel : ViewModel() {
    @Inject
    lateinit var budgetRepository: BudgetRepository

    @Inject
    lateinit var userRepository: UserRepository
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    suspend fun checkForExistingCredentials(): User? {
        return try {
            val user = userRepository.getProfile()
            loadBudgetData()
            user
        } catch (ignored: Exception) {
            null
        }
    }

    suspend fun login(username: String, password: String): User {
        isLoading.value = true
        return try {
            val user = userRepository.login(username, password)
            loadBudgetData()
            user
        } catch (e: java.lang.Exception) {
            isLoading.value = false
            throw e
        }
    }

    private suspend fun loadBudgetData() {
        budgetRepository.prefetchData()
    }
}

