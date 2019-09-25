package com.wbrawner.budget.lib.repository

import com.wbrawner.budget.common.user.User
import com.wbrawner.budget.common.user.UserRepository
import com.wbrawner.budget.lib.network.BudgetApiService
import javax.inject.Inject

class NetworkUserRepository @Inject constructor(private val apiService: BudgetApiService) : UserRepository {
    override suspend fun create(newItem: User): User = apiService.newUser(newItem)

    override suspend fun findAll(accountId: Long?): Collection<User> = apiService.getUsers(accountId)

    override suspend fun findAll(): Collection<User> = findAll(null)

    override suspend fun findById(id: Long): User = apiService.getUser(id)

    override suspend fun findAllByNameLike(query: String): Collection<User> =
            apiService.searchUsers(query)

    override suspend fun update(updatedItem: User): User =
            apiService.updateUser(updatedItem.id!!, updatedItem)

    override suspend fun delete(id: Long) = apiService.deleteUser(id)
}