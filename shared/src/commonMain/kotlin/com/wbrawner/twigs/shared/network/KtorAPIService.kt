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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.path

class KtorAPIService(
    private val client: HttpClient
) : APIService {
    override var baseUrl: String? = null
    override var authToken: String? = null

    override suspend fun getBudgets(count: Int?, page: Int?): List<Budget> = request("budgets")

    override suspend fun getBudget(id: String): Budget = request("budgets/$id")

    override suspend fun newBudget(budget: NewBudgetRequest): Budget = request(
        path = "budgets",
        body = budget,
        httpMethod = HttpMethod.Post
    )

    override suspend fun updateBudget(id: String, budget: Budget): Budget = request(
        path = "budgets/$id",
        body = budget,
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
        body = category,
        httpMethod = HttpMethod.Post
    )

    override suspend fun updateCategory(id: String, category: Category): Category = request(
        path = "categories/$id",
        body = category,
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
        body = transaction,
        httpMethod = HttpMethod.Post
    )

    override suspend fun updateTransaction(id: String, transaction: Transaction): Transaction =
        request(
            path = "transactions/$id",
            body = transaction,
            httpMethod = HttpMethod.Put
        )

    override suspend fun deleteTransaction(id: String) = request<Unit>(
        path = "transactions/$id",
        httpMethod = HttpMethod.Delete
    )

    override suspend fun getRecurringTransactions(
        budgetId: String,
        count: Int?,
        page: Int?
    ): List<RecurringTransaction> = request(
        path = "recurringtransactions",
        queryParams = listOf(
            "budgetId" to budgetId,
            "count" to count,
            "page" to page,
        )
    )

    override suspend fun getRecurringTransaction(id: String): RecurringTransaction =
        request("recurringtransactions/$id")

    override suspend fun newRecurringTransaction(transaction: RecurringTransaction): RecurringTransaction =
        request(
            path = "recurringtransactions",
            body = transaction,
            httpMethod = HttpMethod.Post
        )

    override suspend fun updateRecurringTransaction(
        id: String,
        transaction: RecurringTransaction
    ): RecurringTransaction =
        request(
            path = "recurringtransactions/$id",
            body = transaction,
            httpMethod = HttpMethod.Put
        )

    override suspend fun deleteRecurringTransaction(id: String) = request<Unit>(
        path = "recurringtransactions/$id",
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
        body = request,
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
        body = user,
        httpMethod = HttpMethod.Post,
    )

    override suspend fun updateUser(id: String, user: User): User = request(
        path = "users/$id",
        body = user,
        httpMethod = HttpMethod.Put,
    )

    override suspend fun deleteUser(id: String) = request<Unit>(
        path = "users/$id",
        httpMethod = HttpMethod.Delete
    )

    private suspend inline fun <reified T> request(
        path: String,
        queryParams: List<Pair<String, Any?>>? = null,
        body: Any? = null,
        httpMethod: HttpMethod = HttpMethod.Get
    ): T = client.request(URLBuilder(baseUrl!!).apply { path("api/$path") }.buildString()) {
        method = httpMethod
        headers {
            authToken?.let { append(HttpHeaders.Authorization, "Bearer $it") }
        }
        queryParams?.forEach { (param, value) ->
            value?.let {
                when (it) {
                    is Array<*> -> parameter(param, it.joinToString(","))
                    is Iterable<*> -> parameter(param, it.joinToString(","))
                    else -> parameter(param, it)
                }
            }
        }
        body?.let {
            contentType(ContentType.Application.Json)
            setBody(it)
        }
    }.body()
}