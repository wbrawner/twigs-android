package com.wbrawner.budget.ui

import androidx.lifecycle.ViewModel
import com.wbrawner.budget.auth.CredentialsProvider
import com.wbrawner.budget.common.account.AccountRepository
import com.wbrawner.budget.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class SplashViewModel @Inject constructor(
        private val credentialsProvider: CredentialsProvider,
        private val accountRepository: AccountRepository
) : ViewModel() {
    val isLoading: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    fun checkForExistingCredentials(): Single<Boolean> = Single.create { subscriber ->
        login(credentialsProvider.username(), credentialsProvider.password()).subscribe { _, err ->
            subscriber.onSuccess(err == null)
        }
    }

    fun login(username: String, password: String): Single<Boolean> = Single.create { subscriber ->
        isLoading.onNext(true)
        credentialsProvider.saveCredentials(username, password)
        accountRepository.findAll().subscribe { _, err ->
            isLoading.onNext(false)
            if (err != null) {
                subscriber.onError(err)
            } else {
                subscriber.onSuccess(true)
            }
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