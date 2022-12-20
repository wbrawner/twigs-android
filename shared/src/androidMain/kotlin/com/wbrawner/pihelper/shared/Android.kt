package com.wbrawner.pihelper.shared

import com.wbrawner.twigs.shared.Reducer
import com.wbrawner.twigs.shared.Store
import com.wbrawner.twigs.shared.budget.BudgetReducer
import com.wbrawner.twigs.shared.budget.NetworkBudgetRepository
import com.wbrawner.twigs.shared.network.KtorAPIService
import com.wbrawner.twigs.shared.network.commonConfig
import io.ktor.client.*
import io.ktor.client.engine.android.*

val apiService = KtorAPIService(HttpClient(Android) {
    commonConfig()
})

val budgetRepository = NetworkBudgetRepository(apiService)

fun Store.Companion.create(
    reducers: List<Reducer> = listOf(
        BudgetReducer(budgetRepository)
    ),
) = Store(reducers)
