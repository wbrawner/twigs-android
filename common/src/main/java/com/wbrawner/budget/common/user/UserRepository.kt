package com.wbrawner.budget.common.user

import androidx.lifecycle.LiveData
import com.wbrawner.budget.common.Repository

interface UserRepository : Repository<User, String> {
    val currentUser: LiveData<User?>
    suspend fun login(username: String, password: String): User
    suspend fun getProfile(): User
    suspend fun findAll(budgetId: String? = null): List<User>
    suspend fun findAllByNameLike(query: String): List<User>
}