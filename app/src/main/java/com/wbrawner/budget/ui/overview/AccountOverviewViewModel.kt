package com.wbrawner.budget.ui.overview

import androidx.lifecycle.ViewModel
import com.wbrawner.budget.common.account.AccountRepository
import com.wbrawner.budget.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.reactivex.Single
import javax.inject.Inject

class AccountOverviewViewModel @Inject constructor(private val accountRepo: AccountRepository) : ViewModel() {
    fun getBalance(id: Long): Single<Long> = accountRepo.getBalance(id)
}

@Module
abstract class AccountOverviewViewModelMapper {
    @Binds
    @IntoMap
    @ViewModelKey(AccountOverviewViewModel::class)
    abstract fun bindAccountOverviewViewModel(accountOverviewViewModel: AccountOverviewViewModel): ViewModel
}