package com.wbrawner.budget.lib.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wbrawner.budget.common.PREF_KEY_TOKEN
import com.wbrawner.budget.common.PREF_KEY_USER_ID
import com.wbrawner.budget.common.user.LoginRequest
import com.wbrawner.budget.common.user.User
import com.wbrawner.budget.common.user.UserRepository
import com.wbrawner.budget.lib.network.TwigsApiService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class NetworkUserRepository @Inject constructor(
        private val apiService: TwigsApiService,
        private val sharedPreferences: SharedPreferences
) : UserRepository {

    override val currentUser: LiveData<User?> = MutableLiveData()

    init {
        GlobalScope.launch {
            sharedPreferences.getString(PREF_KEY_USER_ID, null)
                ?.let {
                    try {
                        getProfile()
                    } catch (ignored: Exception) {
                        sharedPreferences.edit()
                            .remove(PREF_KEY_USER_ID)
                            .remove(PREF_KEY_TOKEN)
                            .apply()
                    }
                }
        }
    }

    override suspend fun login(username: String, password: String): User {
        apiService.login(LoginRequest(username, password)).also {
            sharedPreferences.edit()
                .putString(PREF_KEY_USER_ID, it.userId)
                .putString(PREF_KEY_TOKEN, it.token)
                .apply()
        }
        return getProfile()
    }

    override suspend fun getProfile(): User {
        val userId = sharedPreferences.getString(PREF_KEY_USER_ID, null)
            ?: throw RuntimeException("Not authenticated")
        return apiService.getUser(userId).also {
            (currentUser as MutableLiveData).postValue(it)
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
