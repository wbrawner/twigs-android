package com.wbrawner.budget.lib.repository

import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.lib.network.BudgetApiService
import javax.inject.Inject

class NetworkBudgetRepository @Inject constructor(private val apiService: BudgetApiService) :
        BudgetRepository {
    override suspend fun create(newItem: Budget): Budget =
            apiService.newBudget(NewBudgetRequest(newItem))

    override suspend fun findAll(): List<Budget> = apiService.getBudgets().sortedBy { it.name }

    override suspend fun findById(id: Long): Budget = apiService.getBudget(id)

    override suspend fun update(updatedItem: Budget): Budget =
            apiService.updateBudget(updatedItem.id!!, updatedItem)

    override suspend fun delete(id: Long) = apiService.deleteBudget(id)

    override suspend fun getBalance(id: Long): Long = apiService.getBudgetBalance(id).balance
}

data class NewBudgetRequest(
        val name: String,
        val description: String? = null,
        val userIds: List<Long>
) {
    constructor(budget: Budget) : this(budget.name, budget.description, budget.users.map { it.id!! })
}