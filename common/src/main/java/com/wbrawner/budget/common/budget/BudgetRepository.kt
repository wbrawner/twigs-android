package com.wbrawner.budget.common.budget

import com.wbrawner.budget.common.Repository
import kotlinx.coroutines.flow.SharedFlow

interface BudgetRepository : Repository<Budget, String> {
    val currentBudget: SharedFlow<Budget?>
    override suspend fun findById(id: String): Budget = findById(id, false)
    suspend fun findById(id: String, setCurrent: Boolean = false): Budget
    suspend fun prefetchData()
    suspend fun getBalance(id: String): Long
}