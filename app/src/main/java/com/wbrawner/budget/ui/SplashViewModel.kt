package com.wbrawner.budget.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wbrawner.budget.common.user.User
import com.wbrawner.budget.common.user.UserRepository
import com.wbrawner.budget.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject

class SplashViewModel @Inject constructor(
        private val userRepository: UserRepository
) : ViewModel() {
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

@Module
abstract class SplashViewModelMapper {
    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel::class)
    abstract fun bindViewModel(viewModel: SplashViewModel): ViewModel
}