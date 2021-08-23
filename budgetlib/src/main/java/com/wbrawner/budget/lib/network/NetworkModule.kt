package com.wbrawner.budget.lib.network

import android.content.SharedPreferences
import com.wbrawner.budget.common.PREF_KEY_TOKEN
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.common.transaction.TransactionRepository
import com.wbrawner.budget.common.user.UserRepository
import com.wbrawner.budget.lib.repository.NetworkBudgetRepository
import com.wbrawner.budget.lib.repository.NetworkCategoryRepository
import com.wbrawner.budget.lib.repository.NetworkTransactionRepository
import com.wbrawner.budget.lib.repository.NetworkUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

const val PREF_KEY_BASE_URL = "baseUrl"

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Singleton
    @Provides
    fun provideApiService(sharedPreferences: SharedPreferences): TwigsApiService =
        KtorTwigsApiService(
            sharedPreferences.getString(PREF_KEY_BASE_URL, null),
            sharedPreferences.getString(PREF_KEY_TOKEN, null)
        )

    @Singleton
    @Provides
    fun provideBudgetRepository(
        apiService: TwigsApiService,
        sharedPreferences: SharedPreferences
    ): BudgetRepository =
        NetworkBudgetRepository(apiService, sharedPreferences)

    @Singleton
    @Provides
    fun provideCategoryRepository(apiService: TwigsApiService): CategoryRepository =
        NetworkCategoryRepository(apiService)

    @Singleton
    @Provides
    fun provideTransactionRepository(apiService: TwigsApiService): TransactionRepository =
        NetworkTransactionRepository(apiService)

    @Singleton
    @Provides
    fun provideUserRepository(
        apiService: TwigsApiService,
        sharedPreferences: SharedPreferences
    ): UserRepository =
        NetworkUserRepository(apiService, sharedPreferences)
}
