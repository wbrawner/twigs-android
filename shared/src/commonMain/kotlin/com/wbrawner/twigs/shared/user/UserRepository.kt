package com.wbrawner.twigs.shared.user

import com.wbrawner.twigs.shared.Repository

interface UserRepository : Repository<User> {
    suspend fun login(username: String, password: String): User
    suspend fun getProfile(): User
    suspend fun findAll(budgetId: String? = null): List<User>
    suspend fun findAllByNameLike(query: String): List<User>
}