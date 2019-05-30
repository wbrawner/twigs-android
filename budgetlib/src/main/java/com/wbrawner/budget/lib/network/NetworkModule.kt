package com.wbrawner.budget.lib.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.wbrawner.budget.auth.CredentialsProvider
import com.wbrawner.budget.common.account.AccountRepository
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.common.transaction.TransactionRepository
import com.wbrawner.budget.common.user.UserRepository
import com.wbrawner.budget.lib.repository.NetworkAccountRepository
import com.wbrawner.budget.lib.repository.NetworkCategoryRepository
import com.wbrawner.budget.lib.repository.NetworkTransactionRepository
import com.wbrawner.budget.lib.repository.NetworkUserRepository
import dagger.Module
import dagger.Provides
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import javax.inject.Named

@Module
class NetworkModule {
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .build()

    @Provides
    fun provideOkHttpClient(credentialsProvider: CredentialsProvider): OkHttpClient = OkHttpClient.Builder()
            .addInterceptor {
                if (it.request().headers().get("Authorization") != null)
                    return@addInterceptor it.proceed(it.request())
                val credentials = Credentials.basic(
                        credentialsProvider.username(),
                        credentialsProvider.password()
                )
                val newHeaders = it.request()
                        .headers()
                        .newBuilder()
                        .add("Authorization: $credentials")
                        .build()
                val newRequest = it.request()
                        .newBuilder()
                        .headers(newHeaders)
                        .build()
                it.proceed(newRequest)
            }
            .build()

    @Provides
    fun provideRetrofit(
            @Named("baseUrl") baseUrl: String,
            moshi: Moshi,
            client: OkHttpClient
    ): Retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUrl)
            .client(client)
            .build()

    @Provides
    fun provideApiService(retrofit: Retrofit): BudgetApiService =
            retrofit.create(BudgetApiService::class.java)

    @Provides
    fun provideAccountRepository(apiService: BudgetApiService): AccountRepository =
            NetworkAccountRepository(apiService)

    @Provides
    fun provideCategoryRepository(apiService: BudgetApiService): CategoryRepository =
            NetworkCategoryRepository(apiService)

    @Provides
    fun provideTransactionRepository(apiService: BudgetApiService): TransactionRepository =
            NetworkTransactionRepository(apiService)

    @Provides
    fun provideUserRepository(apiService: BudgetApiService): UserRepository =
            NetworkUserRepository(apiService)
}