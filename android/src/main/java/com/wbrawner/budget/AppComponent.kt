package com.wbrawner.budget

import android.content.Context
import com.wbrawner.budget.common.util.ErrorHandler
import com.wbrawner.budget.lib.network.NetworkModule
import com.wbrawner.budget.storage.StorageModule
import com.wbrawner.budget.ui.SplashViewModel
import com.wbrawner.budget.ui.budgets.BudgetFormViewModel
import com.wbrawner.budget.ui.budgets.BudgetListViewModel
import com.wbrawner.budget.ui.categories.CategoryDetailsViewModel
import com.wbrawner.budget.ui.categories.CategoryFormViewModel
import com.wbrawner.budget.ui.categories.CategoryListViewModel
import com.wbrawner.budget.ui.overview.OverviewViewModel
import com.wbrawner.budget.ui.transactions.TransactionFormViewModel
import com.wbrawner.budget.ui.transactions.TransactionListViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, StorageModule::class, NetworkModule::class])
interface AppComponent {
    fun inject(viewModel: OverviewViewModel)
    fun inject(viewModel: SplashViewModel)
    fun inject(viewMode: BudgetListViewModel)
    fun inject(viewModel: BudgetFormViewModel)
    fun inject(viewModel: CategoryListViewModel)
    fun inject(viewModel: CategoryDetailsViewModel)
    fun inject(viewModel: CategoryFormViewModel)
    fun inject(viewModel: TransactionListViewModel)
    fun inject(viewModel: TransactionFormViewModel)

    @Singleton
    val errorHandler: ErrorHandler

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): AppComponent
    }
}
