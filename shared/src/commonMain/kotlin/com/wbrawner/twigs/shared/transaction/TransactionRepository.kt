package com.wbrawner.twigs.shared.transaction

import com.wbrawner.twigs.shared.Repository
import com.wbrawner.twigs.shared.endOfMonth
import com.wbrawner.twigs.shared.network.APIService
import com.wbrawner.twigs.shared.startOfMonth
import kotlinx.datetime.Instant

interface TransactionRepository : Repository<Transaction> {
    suspend fun findAll(
        budgetIds: List<String>? = null,
        categoryIds: List<String>? = null,
        start: Instant,
        end: Instant
    ): List<Transaction>
}

class NetworkTransactionRepository(private val apiService: APIService) : TransactionRepository {
    override suspend fun findAll(
        budgetIds: List<String>?,
        categoryIds: List<String>?,
        start: Instant,
        end: Instant
    ): List<Transaction> = apiService.getTransactions(
        budgetIds = budgetIds,
        categoryIds = categoryIds,
        from = start,
        to = end
    )

    override suspend fun findAll(): List<Transaction> = findAll(
        start = startOfMonth(),
        end = endOfMonth(),
        budgetIds = null,
        categoryIds = null
    )

    override suspend fun create(newItem: Transaction): Transaction =
        apiService.newTransaction(newItem)

    override suspend fun findById(id: String): Transaction = apiService.getTransaction(id)

    override suspend fun update(updatedItem: Transaction): Transaction =
        apiService.updateTransaction(updatedItem.id!!, updatedItem)

    override suspend fun delete(id: String) = apiService.deleteTransaction(id)
}