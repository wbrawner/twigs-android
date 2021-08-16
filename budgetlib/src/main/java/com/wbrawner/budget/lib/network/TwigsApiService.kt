package com.wbrawner.budget.lib.network

import com.wbrawner.budget.common.Session
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.common.user.LoginRequest
import com.wbrawner.budget.common.user.User
import com.wbrawner.budget.lib.repository.NewBudgetRequest
import retrofit2.http.*

interface TwigsApiService {
    // Budgets
    @GET("api/budgets")
    suspend fun getBudgets(
            @Query("count") count: Int? = null,
            @Query("page") page: Int? = null
    ): List<Budget>

    @GET("api/budgets/{id}")
    suspend fun getBudget(@Path("id") id: String): Budget

    @POST("api/budgets")
    suspend fun newBudget(@Body budget: NewBudgetRequest): Budget

    @PUT("api/budgets/{id}")
    suspend fun updateBudget(
            @Path("id") id: String,
            @Body budget: Budget
    ): Budget

    @DELETE("api/budgets/{id}")
    suspend fun deleteBudget(@Path("id") id: String)

    // Categories
    @GET("api/categories")
    suspend fun getCategories(
        @Query("budgetIds") budgetIds: Array<String>? = null,
        @Query("archived") archived: Boolean? = false,
        @Query("count") count: Int? = null,
        @Query("page") page: Int? = null,
    ): List<Category>

    @GET("api/categories/{id}")
    suspend fun getCategory(@Path("id") id: String): Category

    @POST("api/categories")
    suspend fun newCategory(@Body category: Category): Category

    @PUT("api/categories/{id}")
    suspend fun updateCategory(
            @Path("id") id: String,
            @Body category: Category
    ): Category

    @DELETE("api/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String)

    // Transactions
    @GET("api/transactions")
    suspend fun getTransactions(
        @Query("budgetIds") budgetIds: List<String>? = null,
        @Query("categoryIds") categoryIds: List<String>? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("count") count: Int? = null,
        @Query("page") page: Int? = null
    ): List<Transaction>

    @GET("api/transactions/{id}")
    suspend fun getTransaction(@Path("id") id: String): Transaction

    @GET("api/transactions/sum")
    suspend fun sumTransactions(
        @Query("budgetId") budgetId: String? = null,
        @Query("categoryId") categoryId: String? = null
    ): BalanceResponse

    @POST("api/transactions")
    suspend fun newTransaction(@Body transaction: Transaction): Transaction

    @PUT("api/transactions/{id}")
    suspend fun updateTransaction(
        @Path("id") id: String,
        @Body transaction: Transaction
    ): Transaction

    @DELETE("api/transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: String)

    // Users
    @GET("api/users")
    suspend fun getUsers(
            @Query("budgetId") budgetid: String? = null,
            @Query("count") count: Int? = null,
            @Query("page") page: Int? = null
    ): List<User>

    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): Session

    @GET("api/users/search")
    suspend fun searchUsers(@Query("query") query: String): List<User>

    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") id: String): User

    @POST("api/users")
    suspend fun newUser(@Body user: User): User

    @PUT("api/users/{id}")
    suspend fun updateUser(
            @Path("id") id: String,
            @Body user: User
    ): User

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: String)
}