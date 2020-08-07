package com.wbrawner.budget.lib.repository

import com.wbrawner.budget.common.user.LoginRequest
import com.wbrawner.budget.common.user.User
import com.wbrawner.budget.common.user.UserRepository
import com.wbrawner.budget.lib.network.BudgetApiService
import javax.inject.Inject

class NetworkUserRepository @Inject constructor(private val apiService: BudgetApiService) : UserRepository {

    override suspend fun login(username: String, password: String): User =
            apiService.login(LoginRequest(username, password))

    override suspend fun getProfile(): User = apiService.getProfile()

    override suspend fun create(newItem: User): User = apiService.newUser(newItem)

    override suspend fun findAll(accountId: Long?): List<User> = apiService.getUsers(accountId)

    override suspend fun findAll(): List<User> = findAll(null)

    override suspend fun findById(id: Long): User = apiService.getUser(id)

    override suspend fun findAllByNameLike(query: String): List<User> =
            apiService.searchUsers(query)

    override suspend fun update(updatedItem: User): User =
            apiService.updateUser(updatedItem.id!!, updatedItem)

    override suspend fun delete(id: Long) = apiService.deleteUser(id)
}