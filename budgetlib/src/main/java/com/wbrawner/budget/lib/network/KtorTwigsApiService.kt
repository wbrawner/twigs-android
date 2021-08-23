package com.wbrawner.budget.lib.network

import com.wbrawner.budget.common.Session
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.transaction.BalanceResponse
import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.common.user.LoginRequest
import com.wbrawner.budget.common.user.User
import com.wbrawner.budget.lib.repository.NewBudgetRequest
import com.wbrawner.budgetlib.BuildConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*

class KtorTwigsApiService(
    override var baseUrl: String?,
    override var authToken: String?
) : TwigsApiService {
    private val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()

        }
        if (BuildConfig.DEBUG) {
            install(Logging) {
                logger = Logger.ANDROID
                level = LogLevel.ALL
            }
        }
    }

    override suspend fun getBudgets(count: Int?, page: Int?): List<Budget> = request("budgets")

    override suspend fun getBudget(id: String): Budget = request("budgets/$id")

    override suspend fun newBudget(budget: NewBudgetRequest): Budget = request(
        path = "budgets",
        httpBody = budget,
        httpMethod = HttpMethod.Post
    )

    override suspend fun updateBudget(id: String, budget: Budget): Budget = request(
        path = "budgets/$id",
        httpBody = budget,
        httpMethod = HttpMethod.Put
    )

    override suspend fun deleteBudget(id: String) = request<Unit>(
        path = "budgets/$id",
        httpMethod = HttpMethod.Delete
    )

    override suspend fun getCategories(
        budgetIds: Array<String>?,
        archived: Boolean?,
        count: Int?,
        page: Int?
    ): List<Category> = request(
        path = "categories",
        queryParams = listOf(
            "budgetIds" to budgetIds,
            "archived" to archived,
            "count" to count,
            "page" to page
        )
    )

    override suspend fun getCategory(id: String): Category = request("categories/$id")

    override suspend fun newCategory(category: Category): Category = request(
        path = "categories",
        httpBody = category,
        httpMethod = HttpMethod.Post
    )

    override suspend fun updateCategory(id: String, category: Category): Category = request(
        path = "categories/$id",
        httpBody = category,
        httpMethod = HttpMethod.Put
    )

    override suspend fun deleteCategory(id: String) = request<Unit>(
        path = "categories/$id",
        httpMethod = HttpMethod.Delete
    )

    override suspend fun getTransactions(
        budgetIds: List<String>?,
        categoryIds: List<String>?,
        from: String?,
        to: String?,
        count: Int?,
        page: Int?
    ): List<Transaction> = request(
        path = "transactions",
        queryParams = listOf(
            "budgetIds" to budgetIds,
            "categoryIds" to categoryIds,
            "from" to from,
            "to" to to,
            "count" to count,
            "page" to page,
        )
    )

    override suspend fun getTransaction(id: String): Transaction = request("transactions/$id")

    override suspend fun sumTransactions(budgetId: String?, categoryId: String?): BalanceResponse =
        request(
            path = "transactions/sum",
            queryParams = listOf(
                "budgetId" to budgetId,
                "categoryId" to categoryId
            )
        )

    override suspend fun newTransaction(transaction: Transaction): Transaction = request(
        path = "transactions",
        httpBody = transaction,
        httpMethod = HttpMethod.Post
    )

    override suspend fun updateTransaction(id: String, transaction: Transaction): Transaction =
        request(
            path = "transactions/$id",
            httpBody = transaction,
            httpMethod = HttpMethod.Put
        )

    override suspend fun deleteTransaction(id: String) = request<Unit>(
        path = "transactions/$id",
        httpMethod = HttpMethod.Delete
    )

    override suspend fun getUsers(budgetId: String?, count: Int?, page: Int?): List<User> = request(
        path = "users",
        queryParams = listOf(
            "budgetId" to budgetId,
            "count" to count,
            "page" to page
        )
    )

    override suspend fun login(request: LoginRequest): Session = request(
        path = "users/login",
        httpBody = request,
        httpMethod = HttpMethod.Post
    )

    override suspend fun searchUsers(query: String): List<User> = request(
        path = "users",
        queryParams = listOf(
            "query" to query
        )
    )

    override suspend fun getUser(id: String): User = request(
        path = "users/$id"
    )

    override suspend fun newUser(user: User): User = request(
        path = "users",
        httpBody = user,
        httpMethod = HttpMethod.Post,
    )

    override suspend fun updateUser(id: String, user: User): User = request(
        path = "users/$id",
        httpBody = user,
        httpMethod = HttpMethod.Put,
    )

    override suspend fun deleteUser(id: String) = request<Unit>(
        path = "users/$id",
        httpMethod = HttpMethod.Delete
    )

    private suspend inline fun <reified T> request(
        path: String,
        queryParams: List<Pair<String, Any?>>? = null,
        httpBody: Any? = null,
        httpMethod: HttpMethod = HttpMethod.Get
    ): T = client.request(URLBuilder(baseUrl!!).path("api/$path").build()) {
        method = httpMethod
        headers {
            authToken?.let { append(HttpHeaders.Authorization, "Bearer $it") }
        }
        queryParams?.forEach { (param, value) ->
            value?.let {
                when(it) {
                    is Array<*> -> parameter(param, it.joinToString(","))
                    is Iterable<*> -> parameter(param, it.joinToString(","))
                    else -> parameter(param, it)
                }
            }
        }
        httpBody?.let {
            contentType(ContentType.Application.Json)
            body = it
        }
    }
}