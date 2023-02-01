package com.wbrawner.pihelper.shared

import com.russhwolf.settings.Settings
import com.wbrawner.twigs.shared.Reducer
import com.wbrawner.twigs.shared.Store
import com.wbrawner.twigs.shared.budget.BudgetReducer
import com.wbrawner.twigs.shared.budget.NetworkBudgetRepository
import com.wbrawner.twigs.shared.category.CategoryReducer
import com.wbrawner.twigs.shared.category.NetworkCategoryRepository
import com.wbrawner.twigs.shared.network.KtorAPIService
import com.wbrawner.twigs.shared.network.commonConfig
import com.wbrawner.twigs.shared.recurringtransaction.NetworkRecurringTransactionRepository
import com.wbrawner.twigs.shared.recurringtransaction.RecurringTransactionReducer
import com.wbrawner.twigs.shared.transaction.NetworkTransactionRepository
import com.wbrawner.twigs.shared.transaction.TransactionReducer
import com.wbrawner.twigs.shared.user.ConfigReducer
import com.wbrawner.twigs.shared.user.NetworkUserRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android

val apiService = KtorAPIService(HttpClient(Android) {
    commonConfig()
})

val preferences = Settings()

val budgetRepository = NetworkBudgetRepository(apiService)
val categoryRepository = NetworkCategoryRepository(apiService)
val transactionRepository = NetworkTransactionRepository(apiService)
val recurringTransactionRepository = NetworkRecurringTransactionRepository(apiService)
val userRepository = NetworkUserRepository(apiService)

fun Store.Companion.create(
    reducers: List<Reducer> = listOf(
        BudgetReducer(budgetRepository, preferences),
        CategoryReducer(categoryRepository),
        ConfigReducer(apiService, preferences),
        TransactionReducer(transactionRepository, userRepository),
        RecurringTransactionReducer(recurringTransactionRepository, userRepository)
    ),
) = Store(reducers)
