package com.wbrawner.budget.lib.network

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.common.transaction.TransactionRepository
import com.wbrawner.budget.common.user.UserRepository
import com.wbrawner.budget.lib.repository.NetworkBudgetRepository
import com.wbrawner.budget.lib.repository.NetworkCategoryRepository
import com.wbrawner.budget.lib.repository.NetworkTransactionRepository
import com.wbrawner.budget.lib.repository.NetworkUserRepository
import com.wbrawner.budgetlib.BuildConfig
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import javax.inject.Singleton

const val PREF_KEY_BASE_URL = "baseUrl"

@Module
class NetworkModule {
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .build()

    @Singleton
    @Provides
    fun provideBaseUrlHelper(sharedPreferences: SharedPreferences) = BaseUrlHelper().apply {
        sharedPreferences.getString(PREF_KEY_BASE_URL, null)?.let {
            url = it
        }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(baseUrlHelper: BaseUrlHelper, authHelper: AuthHelper): OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(baseUrlHelper.interceptor)
            .addInterceptor(authHelper.interceptor)
            .apply {
                if (BuildConfig.DEBUG)
                    this.addInterceptor(
                            HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
                                    .setLevel(HttpLoggingInterceptor.Level.BODY)
                    )
            }
            .build()

    @Provides
    fun provideRetrofit(
            moshi: Moshi,
            client: OkHttpClient
    ): Retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(DEFAULT_URL)
            .client(client)
            .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): TwigsApiService =
            retrofit.create(TwigsApiService::class.java)

    @Singleton
    @Provides
    fun provideBudgetRepository(apiService: TwigsApiService, sharedPreferences: SharedPreferences): BudgetRepository =
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
    fun provideUserRepository(apiService: TwigsApiService, sharedPreferences: SharedPreferences): UserRepository =
            NetworkUserRepository(apiService, sharedPreferences)
}
