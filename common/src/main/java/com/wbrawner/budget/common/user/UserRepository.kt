package com.wbrawner.budget.common.user

import com.wbrawner.budget.common.Repository

interface UserRepository : Repository<User, Long> {
    suspend fun login(username: String, password: String): User
    suspend fun getProfile(): User
    suspend fun findAll(accountId: Long? = null): Collection<User>
    suspend fun findAllByNameLike(query: String): Collection<User>
}