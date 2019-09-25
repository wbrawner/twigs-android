package com.wbrawner.budget.lib.repository

import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.common.transaction.TransactionRepository
import com.wbrawner.budget.lib.network.BudgetApiService
import javax.inject.Inject

class NetworkTransactionRepository @Inject constructor(private val apiService: BudgetApiService) : TransactionRepository {
    override suspend fun create(newItem: Transaction): Transaction = apiService.newTransaction(newItem)

    override suspend fun findAll(budgetId: Long?, categoryId: Long?): Collection<Transaction> =
            apiService.getTransactions(budgetId, categoryId).sortedByDescending { it.date }

    override suspend fun findAll(): Collection<Transaction> = findAll(null)

    override suspend fun findById(id: Long): Transaction = apiService.getTransaction(id)

    override suspend fun update(updatedItem: Transaction): Transaction =
            apiService.updateTransaction(updatedItem.id!!, updatedItem)

    override suspend fun delete(id: Long) = apiService.deleteTransaction(id)
}
