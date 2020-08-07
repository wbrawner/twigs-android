package com.wbrawner.budget

import android.content.Context
import com.wbrawner.budget.lib.network.NetworkModule
import com.wbrawner.budget.storage.StorageModule
import com.wbrawner.budget.ui.SplashViewModel
import com.wbrawner.budget.ui.budgets.BudgetFormViewModel
import com.wbrawner.budget.ui.budgets.BudgetViewModel
import com.wbrawner.budget.ui.categories.CategoryViewModel
import com.wbrawner.budget.ui.overview.OverviewViewModel
import com.wbrawner.budget.ui.transactions.TransactionFormViewModel
import com.wbrawner.budget.ui.transactions.TransactionListViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [StorageModule::class, NetworkModule::class])
interface AppComponent {
    fun inject(viewModel: OverviewViewModel)
    fun inject(viewModel: SplashViewModel)
    fun inject(viewMode: BudgetViewModel)
    fun inject(viewModel: BudgetFormViewModel)
    fun inject(viewModel: CategoryViewModel)
    fun inject(viewModel: TransactionListViewModel)
    fun inject(viewModel: TransactionFormViewModel)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun baseUrl(@Named("baseUrl") baseUrl: String): Builder

        @BindsInstance
        fun context(context: Context): Builder
        fun build(): AppComponent
    }
}
