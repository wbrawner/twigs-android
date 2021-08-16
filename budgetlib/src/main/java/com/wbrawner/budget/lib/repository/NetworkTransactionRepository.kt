package com.wbrawner.budget.lib.repository

import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.common.transaction.TransactionRepository
import com.wbrawner.budget.lib.network.TwigsApiService
import java.util.*
import javax.inject.Inject

class NetworkTransactionRepository @Inject constructor(private val apiService: TwigsApiService) :
    TransactionRepository {

    override suspend fun create(newItem: Transaction): Transaction =
        apiService.newTransaction(newItem)

    override suspend fun findAll(
        budgetIds: List<String>?,
        categoryIds: List<String>?,
        start: Calendar?,
        end: Calendar?
    ): List<Transaction> = apiService.getTransactions(
        budgetIds,
        categoryIds,
        start?.toInstant()?.toString(),
        end?.toInstant()?.toString()
    )

    override suspend fun findAll(): List<Transaction> = findAll(null)

    override suspend fun findById(id: String): Transaction = apiService.getTransaction(id)

    override suspend fun update(updatedItem: Transaction): Transaction =
        apiService.updateTransaction(updatedItem.id!!, updatedItem)

    override suspend fun delete(id: String) = apiService.deleteTransaction(id)
}
