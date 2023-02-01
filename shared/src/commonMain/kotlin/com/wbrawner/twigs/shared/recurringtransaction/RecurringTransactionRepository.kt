package com.wbrawner.twigs.shared.recurringtransaction

import com.wbrawner.twigs.shared.Repository
import com.wbrawner.twigs.shared.network.APIService

interface RecurringTransactionRepository : Repository<RecurringTransaction> {
    suspend fun findAll(
        budgetId: String,
    ): List<RecurringTransaction>
}

class NetworkRecurringTransactionRepository(private val apiService: APIService) :
    RecurringTransactionRepository {
    override suspend fun findAll(
        budgetId: String,
    ): List<RecurringTransaction> = apiService.getRecurringTransactions(
        budgetId,
    )

    override suspend fun findAll(): List<RecurringTransaction> = TODO("Not yet implemented")

    override suspend fun create(newItem: RecurringTransaction): RecurringTransaction =
        apiService.newRecurringTransaction(newItem)

    override suspend fun findById(id: String): RecurringTransaction =
        apiService.getRecurringTransaction(id)

    override suspend fun update(updatedItem: RecurringTransaction): RecurringTransaction =
        apiService.updateRecurringTransaction(updatedItem.id!!, updatedItem)

    override suspend fun delete(id: String) = apiService.deleteTransaction(id)
}