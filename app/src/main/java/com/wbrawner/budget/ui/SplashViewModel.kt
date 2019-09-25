package com.wbrawner.budget.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wbrawner.budget.auth.CredentialsProvider
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.user.User
import com.wbrawner.budget.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject

class SplashViewModel @Inject constructor(
        private val credentialsProvider: CredentialsProvider,
        private val budgetRepository: BudgetRepository
) : ViewModel() {
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    suspend fun checkForExistingCredentials(): User? {
        return try {
            login(credentialsProvider.username, credentialsProvider.password)
        } catch (ignored: Exception) {
            null
        }
    }

    suspend fun login(username: String, password: String): User {
        isLoading.value = true
        credentialsProvider.saveCredentials(username, password)
        return try {
            budgetRepository.login(username, password)
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