package com.wbrawner.budget.common.user

import com.wbrawner.budget.common.Repository
import com.wbrawner.budget.common.Session
import kotlinx.coroutines.flow.SharedFlow

interface UserRepository : Repository<User, String> {
    val currentUser: SharedFlow<User?>
    suspend fun login(server: String, username: String, password: String): Session
    suspend fun getProfile(): User
    suspend fun findAll(budgetId: String? = null): List<User>
    suspend fun findAllByNameLike(query: String): List<User>
}