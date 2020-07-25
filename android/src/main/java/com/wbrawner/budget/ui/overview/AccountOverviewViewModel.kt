package com.wbrawner.budget.ui.overview

import androidx.lifecycle.ViewModel
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject

class AccountOverviewViewModel @Inject constructor(private val budgetRepo: BudgetRepository) : ViewModel() {
    suspend fun getBalance(id: Long): Long = budgetRepo.getBalance(id)
}

@Module
abstract class AccountOverviewViewModelMapper {
    @Binds
    @IntoMap
    @ViewModelKey(AccountOverviewViewModel::class)
    abstract fun bindAccountOverviewViewModel(accountOverviewViewModel: AccountOverviewViewModel): ViewModel
}