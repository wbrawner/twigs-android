package com.wbrawner.twigs.shared.user

import com.wbrawner.twigs.shared.Repository
import com.wbrawner.twigs.shared.network.APIService

interface UserRepository : Repository<User> {
    suspend fun findAll(budgetId: String? = null): List<User>
    suspend fun findAllByNameLike(query: String): List<User>
}

class NetworkUserRepository(private val apiService: APIService) : UserRepository {
    override suspend fun findAll(budgetId: String?): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun findAll(): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun findAllByNameLike(query: String): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun create(newItem: User): User {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: String): User = apiService.getUser(id)

    override suspend fun update(updatedItem: User): User {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: String) {
        TODO("Not yet implemented")
    }
}