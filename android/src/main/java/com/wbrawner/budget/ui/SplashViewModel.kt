package com.wbrawner.budget.ui

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.user.User
import com.wbrawner.budget.common.user.UserRepository
import com.wbrawner.budget.lib.repository.KEY_DEFAULT_BUDGET
import javax.inject.Inject

class SplashViewModel : ViewModel() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

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
        // TODO: This would fail for a new user that has no budgets
        val budgets = budgetRepository.findAll()
        val budgetId = sharedPreferences.getLong(KEY_DEFAULT_BUDGET, budgets.first().id!!)
        budgetRepository.currentBudget = try {
            budgetRepository.findById(budgetId)
        } catch (e: Exception) {
            // For some reason we can't find the default budget id, so fallback to the first budget
            budgets.first()
        }
    }
}

