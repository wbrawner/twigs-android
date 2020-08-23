package com.wbrawner.budget.common.budget

import androidx.lifecycle.LiveData
import com.wbrawner.budget.common.Repository

interface BudgetRepository : Repository<Budget, Long> {
    val currentBudget: LiveData<Budget?>
    override suspend fun findById(id: Long): Budget = findById(id, false)
    suspend fun findById(id: Long, setCurrent: Boolean = false): Budget
    suspend fun prefetchData()
    suspend fun getBalance(id: Long): Long
}