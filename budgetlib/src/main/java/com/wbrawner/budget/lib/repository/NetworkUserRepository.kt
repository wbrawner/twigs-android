package com.wbrawner.budget.lib.repository

import android.content.SharedPreferences
import com.wbrawner.budget.common.PREF_KEY_USER_ID
import com.wbrawner.budget.common.Session
import com.wbrawner.budget.common.user.LoginRequest
import com.wbrawner.budget.common.user.User
import com.wbrawner.budget.common.user.UserRepository
import com.wbrawner.budget.lib.network.TwigsApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class NetworkUserRepository @Inject constructor(
        private val apiService: TwigsApiService,
        private val sharedPreferences: SharedPreferences
) : UserRepository {

    private val current = MutableStateFlow<User?>(null)
    override val currentUser: SharedFlow<User?> = current.asSharedFlow()

    override suspend fun login(server: String, username: String, password: String): Session {
        apiService.baseUrl = server
        return apiService.login(LoginRequest(username, password)).apply {
            apiService.authToken = token
        }
    }

    override suspend fun getProfile(): User {
        val userId = sharedPreferences.getString(PREF_KEY_USER_ID, null)
            ?: throw RuntimeException("Not authenticated")
        return apiService.getUser(userId).also {
            current.emit(it)
        }
    }

    override suspend fun create(newItem: User): User = apiService.newUser(newItem)

    override suspend fun findAll(budgetId: String?): List<User> = apiService.getUsers(budgetId)

    override suspend fun findAll(): List<User> = findAll(null)

    override suspend fun findById(id: String): User = apiService.getUser(id)

    override suspend fun findAllByNameLike(query: String): List<User> =
            apiService.searchUsers(query)

    override suspend fun update(updatedItem: User): User =
            apiService.updateUser(updatedItem.id!!, updatedItem)

    override suspend fun delete(id: String) = apiService.deleteUser(id)
}
