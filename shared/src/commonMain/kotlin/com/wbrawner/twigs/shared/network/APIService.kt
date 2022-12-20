package com.wbrawner.twigs.shared.network

import com.wbrawner.twigs.shared.budget.Budget
import com.wbrawner.twigs.shared.budget.NewBudgetRequest
import com.wbrawner.twigs.shared.category.Category
import com.wbrawner.twigs.shared.transaction.BalanceResponse
import com.wbrawner.twigs.shared.transaction.Transaction
import com.wbrawner.twigs.shared.user.LoginRequest
import com.wbrawner.twigs.shared.user.Session
import com.wbrawner.twigs.shared.user.User
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

const val BASE_PATH = "/api"

interface APIService {
    var baseUrl: String?
    var authToken: String?

    // Budgets
    suspend fun getBudgets(
        count: Int? = null,
        page: Int? = null
    ): List<Budget>

    suspend fun getBudget(id: String): Budget

    suspend fun newBudget(budget: NewBudgetRequest): Budget

    suspend fun updateBudget(
        id: String,
        budget: Budget
    ): Budget

    suspend fun deleteBudget(id: String)

    // Categories
    suspend fun getCategories(
        budgetIds: Array<String>? = null,
        archived: Boolean? = false,
        count: Int? = null,
        page: Int? = null,
    ): List<Category>

    suspend fun getCategory(id: String): Category

    suspend fun newCategory(category: Category): Category

    suspend fun updateCategory(
        id: String,
        category: Category
    ): Category

    suspend fun deleteCategory(id: String)

    // Transactions
    suspend fun getTransactions(
        budgetIds: List<String>? = null,
        categoryIds: List<String>? = null,
        from: String? = null,
        to: String? = null,
        count: Int? = null,
        page: Int? = null
    ): List<Transaction>

    suspend fun getTransaction(id: String): Transaction

    suspend fun sumTransactions(
        budgetId: String? = null,
        categoryId: String? = null
    ): BalanceResponse

    suspend fun newTransaction(transaction: Transaction): Transaction

    suspend fun updateTransaction(
        id: String,
        transaction: Transaction
    ): Transaction

    suspend fun deleteTransaction(id: String)

    // Users
    suspend fun getUsers(
        budgetId: String? = null,
        count: Int? = null,
        page: Int? = null
    ): List<User>

    suspend fun login(request: LoginRequest): Session

    suspend fun searchUsers(query: String): List<User>

    suspend fun getUser(id: String): User

    suspend fun newUser(user: User): User

    suspend fun updateUser(
        id: String,
        user: User
    ): User

    suspend fun deleteUser(id: String)

    companion object
}

fun <T: HttpClientEngineConfig> HttpClientConfig<T>.commonConfig() {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 1000
        connectTimeoutMillis = 1000
        socketTimeoutMillis = 1000
    }
}
