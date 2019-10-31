package com.wbrawner.budget.di

import android.content.Context
import com.wbrawner.budget.lib.network.NetworkModule
import com.wbrawner.budget.storage.StorageModule
import com.wbrawner.budget.ui.MainActivity
import com.wbrawner.budget.ui.SplashActivity
import com.wbrawner.budget.ui.SplashViewModelMapper
import com.wbrawner.budget.ui.auth.LoginFragment
import com.wbrawner.budget.ui.auth.RegisterFragment
import com.wbrawner.budget.ui.budgets.AddEditAccountsViewModelMapper
import com.wbrawner.budget.ui.budgets.AddEditBudgetFragment
import com.wbrawner.budget.ui.budgets.BudgetListFragment
import com.wbrawner.budget.ui.budgets.BudgetViewModelMapper
import com.wbrawner.budget.ui.categories.AddEditCategoryActivity
import com.wbrawner.budget.ui.categories.CategoryFragment
import com.wbrawner.budget.ui.categories.CategoryListFragment
import com.wbrawner.budget.ui.categories.CategoryViewModelMapper
import com.wbrawner.budget.ui.overview.AccountOverviewViewModelMapper
import com.wbrawner.budget.ui.overview.OverviewFragment
import com.wbrawner.budget.ui.transactions.AddEditTransactionActivity
import com.wbrawner.budget.ui.transactions.AddEditTransactionViewModelMapper
import com.wbrawner.budget.ui.transactions.TransactionListFragment
import com.wbrawner.budget.ui.transactions.TransactionListViewModelMapper
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [StorageModule::class, AppModule::class, NetworkModule::class])
interface AppComponent {
    fun inject(fragment: OverviewFragment)
    fun inject(fragment: LoginFragment)
    fun inject(fragment: BudgetListFragment)
    fun inject(fragment: RegisterFragment)
    fun inject(fragment: AddEditBudgetFragment)
    fun inject(fragment: CategoryFragment)
    fun inject(fragment: TransactionListFragment)
    fun inject(fragment: CategoryListFragment)
    fun inject(activity: MainActivity)
    fun inject(activity: SplashActivity)
    fun inject(activity: AddEditCategoryActivity)
    fun inject(activity: AddEditTransactionActivity)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun baseUrl(@Named("baseUrl") baseUrl: String): Builder

        @BindsInstance
        fun context(context: Context): Builder
        fun build(): AppComponent
    }
}

@Module(
        includes = [
            AccountOverviewViewModelMapper::class,
            AddEditAccountsViewModelMapper::class,
            AddEditTransactionViewModelMapper::class,
            BudgetViewModelMapper::class,
            CategoryViewModelMapper::class,
            SplashViewModelMapper::class,
            TransactionListViewModelMapper::class,
            ViewModelFactoryModule::class
        ]
)
class AppModule