package com.wbrawner.budget.lib.network

import com.wbrawner.budget.common.account.Account
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.common.user.User
import io.reactivex.Single
import retrofit2.http.*

interface BudgetApiService {
    // Accounts
    @GET("accounts")
    fun getAccounts(
            @Query("count") count: Int? = null,
            @Query("page") page: Int? = null
    ): Single<Collection<Account>>

    @GET("accounts/{id}")
    fun getAccount(@Path("id") id: Long): Single<Account>

    @GET("accounts/{id}/balance")
    fun getAccountBalance(@Path("id") id: Long): Single<AccountBalanceResponse>

    @POST("accounts/new")
    fun newAccount(@Body account: Account): Single<Account>

    @PUT("accounts/{id}")
    fun updateAccount(
            @Path("id") id: Long,
            @Body account: Account
    ): Single<Account>

    @DELETE("accounts/{id}")
    fun deleteAccount(@Path("id") id: Long): Single<Void>

    // Categories
    @GET("categories")
    fun getCategories(
            @Query("accountId") accountId: Long,
            @Query("count") count: Int? = null,
            @Query("page") page: Int? = null
    ): Single<Collection<Category>>

    @GET("categories/{id}")
    fun getCategory(@Path("id") id: Long): Single<Category>

    @GET("categories/{id}/balance")
    fun getCategoryBalance(@Path("id") id: Long): Single<CategoryBalanceResponse>

    @POST("categories/new")
    fun newCategory(@Body category: Category): Single<Category>

    @PUT("categories/{id}")
    fun updateCategory(
            @Path("id") id: Long,
            @Body category: Category
    ): Single<Category>

    @DELETE("categories/{id}")
    fun deleteCategory(@Path("id") id: Long): Single<Void>

    // Transactions
    @GET("transactions")
    fun getTransactions(
            @Query("accountId") accountId: Long,
            @Query("count") count: Int? = null,
            @Query("page") page: Int? = null
    ): Single<Collection<Transaction>>

    @GET("transactions/{id}")
    fun getTransaction(@Path("id") id: Long): Single<Transaction>

    @POST("transactions/new")
    fun newTransaction(@Body transaction: Transaction): Single<Transaction>

    @PUT("transactions/{id}")
    fun updateTransaction(
            @Path("id") id: Long,
            @Body transaction: Transaction
    ): Single<Transaction>

    @DELETE("transactions/{id}")
    fun deleteTransaction(@Path("id") id: Long): Single<Void>

    // Users
    @GET("users")
    fun getUsers(
            @Query("accountId") accountId: Long,
            @Query("count") count: Int? = null,
            @Query("page") page: Int? = null
    ): Single<Collection<User>>

    @GET("users/{id}")
    fun getUser(@Path("id") id: Long): Single<User>

    @POST("users/new")
    fun newUser(@Body user: User): Single<User>

    @PUT("users/{id}")
    fun updateUser(
            @Path("id") id: Long,
            @Body user: User
    ): Single<User>

    @DELETE("users/{id}")
    fun deleteUser(@Path("id") id: Long): Single<Void>
}