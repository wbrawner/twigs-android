package com.wbrawner.budget.common.budget

import com.wbrawner.budget.common.Repository
import com.wbrawner.budget.common.user.User

interface BudgetRepository : Repository<Budget, Long> {
    suspend fun login(username: String, password: String): User
    suspend fun getBalance(id: Long): Long
}