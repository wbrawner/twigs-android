package com.wbrawner.twigs.shared.network

import com.wbrawner.twigs.shared.budget.Budget
import com.wbrawner.twigs.shared.budget.NewBudgetRequest
import com.wbrawner.twigs.shared.category.Category
import com.wbrawner.twigs.shared.recurringtransaction.RecurringTransaction
import com.wbrawner.twigs.shared.transaction.BalanceResponse
import com.wbrawner.twigs.shared.transaction.Transaction
import com.wbrawner.twigs.shared.user.LoginRequest
import com.wbrawner.twigs.shared.user.Session
import com.wbrawner.twigs.shared.user.User
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

const val BASE_PATH = "/api"

interface APIService {
    var baseUrl: String?
    var authToken: String?

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

    suspend fun getCategories(
        budgetIds: Array<String>? = null,
        archived: Boolean? = null,
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

    suspend fun getRecurringTransactions(
        budgetId: String,
        count: Int? = null,
        page: Int? = null
    ): List<RecurringTransaction>

    suspend fun getRecurringTransaction(id: String): RecurringTransaction

    suspend fun newRecurringTransaction(transaction: RecurringTransaction): RecurringTransaction

    suspend fun updateRecurringTransaction(
        id: String,
        transaction: RecurringTransaction
    ): RecurringTransaction

    suspend fun deleteRecurringTransaction(id: String)

    suspend fun getTransactions(
        from: Instant,
        to: Instant,
        budgetIds: List<String>? = null,
        categoryIds: List<String>? = null,
        count: Int? = null,
        page: Int? = null
    ): List<Transaction>

    suspend fun getTransaction(id: String): Transaction

    suspend fun sumTransactions(
        from: Instant,
        to: Instant,
        budgetId: String? = null,
        categoryId: String? = null,
    ): BalanceResponse

    suspend fun newTransaction(transaction: Transaction): Transaction

    suspend fun updateTransaction(
        id: String,
        transaction: Transaction
    ): Transaction

    suspend fun deleteTransaction(id: String)

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

fun <T : HttpClientEngineConfig> HttpClientConfig<T>.commonConfig() {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 60_000
        connectTimeoutMillis = 60_000
        socketTimeoutMillis = 60_000
    }
}
