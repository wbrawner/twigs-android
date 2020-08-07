package com.wbrawner.budget.lib.network

import android.content.SharedPreferences
import android.util.Base64
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
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.nio.charset.Charset
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .build()

    @Provides
    fun provideCookieJar(sharedPreferences: SharedPreferences): CookieJar {
        return object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                sharedPreferences.edit()
                        .putString(
                                url.host,
                                cookies.joinToString(separator = ",") {
                                    Base64.encode(it.toString().toByteArray(), 0)
                                            .toString(charset = Charset.forName("UTF-8"))
                                }
                        )
                        .apply()
            }

            override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
                return sharedPreferences.getString(url.host, "")
                        ?.split(",")
                        ?.mapNotNull {
                            Cookie.parse(
                                    url,
                                    Base64.decode(it, 0).toString(Charset.forName("UTF-8"))
                            )
                        }
                        ?.toMutableList()
                        ?: mutableListOf()
            }
        }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(cookieJar: CookieJar): OkHttpClient = OkHttpClient.Builder()
            .cookieJar(cookieJar)
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
            @Named("baseUrl") baseUrl: String,
            moshi: Moshi,
            client: OkHttpClient
    ): Retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(baseUrl)
            .client(client)
            .build()

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): BudgetApiService =
            retrofit.create(BudgetApiService::class.java)

    @Singleton
    @Provides
    fun provideBudgetRepository(apiService: BudgetApiService, sharedPreferences: SharedPreferences): BudgetRepository =
            NetworkBudgetRepository(apiService, sharedPreferences)

    @Singleton
    @Provides
    fun provideCategoryRepository(apiService: BudgetApiService): CategoryRepository =
            NetworkCategoryRepository(apiService)

    @Singleton
    @Provides
    fun provideTransactionRepository(apiService: BudgetApiService): TransactionRepository =
            NetworkTransactionRepository(apiService)

    @Singleton
    @Provides
    fun provideUserRepository(apiService: BudgetApiService): UserRepository =
            NetworkUserRepository(apiService)
}
