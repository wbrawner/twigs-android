package com.wbrawner.budget.lib.network

import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.common.user.LoginRequest
import com.wbrawner.budget.common.user.User
import com.wbrawner.budget.lib.repository.NewBudgetRequest
import retrofit2.http.*

interface BudgetApiService {
    // Budgets
    @GET("budgets")
    suspend fun getBudgets(
            @Query("count") count: Int? = null,
            @Query("page") page: Int? = null
    ): List<Budget>

    @GET("budgets/{id}")
    suspend fun getBudget(@Path("id") id: Long): Budget

    @GET("budgets/{id}/balance")
    suspend fun getBudgetBalance(@Path("id") id: Long): BudgetBalanceResponse

    @POST("budgets")
    suspend fun newBudget(@Body budget: NewBudgetRequest): Budget

    @PUT("budgets/{id}")
    suspend fun updateBudget(
            @Path("id") id: Long,
            @Body budget: Budget
    ): Budget

    @DELETE("budgets/{id}")
    suspend fun deleteBudget(@Path("id") id: Long)

    // Categories
    @GET("categories")
    suspend fun getCategories(
            @Query("budgetIds") budgetIds: Array<Long>? = null,
            @Query("count") count: Int? = null,
            @Query("page") page: Int? = null
    ): List<Category>

    @GET("categories/{id}")
    suspend fun getCategory(@Path("id") id: Long): Category

    @GET("categories/{id}/balance")
    suspend fun getCategoryBalance(@Path("id") id: Long): CategoryBalanceResponse

    @POST("categories")
    suspend fun newCategory(@Body category: Category): Category

    @PUT("categories/{id}")
    suspend fun updateCategory(
            @Path("id") id: Long,
            @Body category: Category
    ): Category

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Long)

    // Transactions
    @GET("transactions")
    suspend fun getTransactions(
            @Query("budgetIds") budgetIds: List<Long>? = null,
            @Query("categoryIds") categoryIds: List<Long>? = null,
            @Query("from") from: String? = null,
            @Query("to") to: String? = null,
            @Query("count") count: Int? = null,
            @Query("page") page: Int? = null
    ): List<Transaction>

    @GET("transactions/{id}")
    suspend fun getTransaction(@Path("id") id: Long): Transaction

    @POST("transactions")
    suspend fun newTransaction(@Body transaction: Transaction): Transaction

    @PUT("transactions/{id}")
    suspend fun updateTransaction(
            @Path("id") id: Long,
            @Body transaction: Transaction
    ): Transaction

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: Long)

    // Users
    @GET("users")
    suspend fun getUsers(
            @Query("budgetId") budgetId: Long? = null,
            @Query("count") count: Int? = null,
            @Query("page") page: Int? = null
    ): List<User>

    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): User

    @GET("users/me")
    suspend fun getProfile(): User

    @GET("users/search")
    suspend fun searchUsers(@Query("query") query: String): List<User>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Long): User

    @POST("users")
    suspend fun newUser(@Body user: User): User

    @PUT("users/{id}")
    suspend fun updateUser(
            @Path("id") id: Long,
            @Body user: User
    ): User

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Long)
}