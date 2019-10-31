package com.wbrawner.budget.common.budget

import com.wbrawner.budget.common.Repository

interface BudgetRepository : Repository<Budget, Long> {
    suspend fun getBalance(id: Long): Long
}