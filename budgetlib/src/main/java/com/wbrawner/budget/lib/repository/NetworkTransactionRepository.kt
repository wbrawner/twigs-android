package com.wbrawner.budget.lib.repository

import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.common.transaction.TransactionRepository
import com.wbrawner.budget.lib.network.BudgetApiService
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class NetworkTransactionRepository @Inject constructor(private val apiService: BudgetApiService) : TransactionRepository {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)

    override suspend fun create(newItem: Transaction): Transaction = apiService.newTransaction(newItem)

    override suspend fun findAll(
            budgetIds: List<Long>?,
            categoryIds: List<Long>?,
            start: Calendar?,
            end: Calendar?
    ): List<Transaction> = apiService.getTransactions(
            budgetIds,
            categoryIds,
            start?.let {
                it.timeZone = TimeZone.getTimeZone("UTC")
                dateFormatter.format(it.time)
            },
            end?.let {
                it.timeZone = TimeZone.getTimeZone("UTC")
                dateFormatter.format(it.time)
            }
    )

    override suspend fun findAll(): List<Transaction> = findAll(null)

    override suspend fun findById(id: Long): Transaction = apiService.getTransaction(id)

    override suspend fun update(updatedItem: Transaction): Transaction =
            apiService.updateTransaction(updatedItem.id!!, updatedItem)

    override suspend fun delete(id: Long) = apiService.deleteTransaction(id)
}
