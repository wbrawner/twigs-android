package com.wbrawner.budget.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wbrawner.budget.common.user.User
import com.wbrawner.budget.common.user.UserRepository
import javax.inject.Inject

class SplashViewModel : ViewModel() {

    @Inject lateinit var userRepository: UserRepository
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    suspend fun checkForExistingCredentials(): User? {
        return try {
            userRepository.getProfile()
        } catch (ignored: Exception) {
            null
        }
    }

    suspend fun login(username: String, password: String): User {
        isLoading.value = true
        return try {
            userRepository.login(username, password)
        } catch (e: java.lang.Exception) {
            isLoading.value = false
            throw e
        }
    }
}
