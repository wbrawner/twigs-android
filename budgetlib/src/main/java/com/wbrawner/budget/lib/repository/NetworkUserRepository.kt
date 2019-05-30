package com.wbrawner.budget.lib.repository

import com.wbrawner.budget.common.user.User
import com.wbrawner.budget.common.user.UserRepository
import com.wbrawner.budget.lib.network.BudgetApiService
import io.reactivex.Single
import javax.inject.Inject

class NetworkUserRepository @Inject constructor(private val apiService: BudgetApiService) : UserRepository {
    override fun create(newItem: User): Single<User> = apiService.newUser(newItem)

    override fun findAll(accountId: Long): Single<Collection<User>> = apiService.getUsers(accountId)

    /**
     * This will only return an empty list, since an accountId is required to get users.
     * Pass a [Long] as the first (and only) parameter to denote the
     * [account ID][com.wbrawner.budget.common.account.Account.id] instead
     */
    override fun findAll(): Single<Collection<User>> = Single.just(ArrayList())

    override fun findById(id: Long): Single<User> = apiService.getUser(id)

    override fun update(updatedItem: User): Single<User> =
            apiService.updateUser(updatedItem.id!!, updatedItem)

    override fun delete(id: Long): Single<Void> = apiService.deleteUser(id)
}