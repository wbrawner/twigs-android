package com.wbrawner.twigs.shared.budget

import com.wbrawner.twigs.shared.Repository
import com.wbrawner.twigs.shared.network.APIService
import kotlinx.datetime.Instant

interface BudgetRepository : Repository<Budget> {
    suspend fun getBalance(id: String, from: Instant, to: Instant): Long
}

class NetworkBudgetRepository(private val apiService: APIService) : BudgetRepository {
    override suspend fun create(newItem: Budget): Budget = apiService.newBudget(NewBudgetRequest(newItem))

    override suspend fun findAll(): List<Budget> = apiService.getBudgets()

    override suspend fun findById(id: String): Budget = apiService.getBudget(id)

    override suspend fun update(updatedItem: Budget): Budget =
        apiService.updateBudget(updatedItem.id!!, updatedItem)

    override suspend fun delete(id: String) = apiService.deleteBudget(id)

    override suspend fun getBalance(id: String, from: Instant, to: Instant): Long =
        apiService.sumTransactions(budgetId = id, from = from, to = to).balance
}