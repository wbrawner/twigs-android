package com.wbrawner.budget.common.budget

import androidx.lifecycle.LiveData
import com.wbrawner.budget.common.Repository

interface BudgetRepository : Repository<Budget, String> {
    val currentBudget: LiveData<Budget?>
    override suspend fun findById(id: String): Budget = findById(id, false)
    suspend fun findById(id: String, setCurrent: Boolean = false): Budget
    suspend fun prefetchData()
    suspend fun getBalance(id: String): Long
}