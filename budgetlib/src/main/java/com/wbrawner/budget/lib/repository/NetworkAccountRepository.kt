package com.wbrawner.budget.lib.repository

import com.wbrawner.budget.common.account.Account
import com.wbrawner.budget.common.account.AccountRepository
import com.wbrawner.budget.lib.network.BudgetApiService
import io.reactivex.Single
import javax.inject.Inject

class NetworkAccountRepository @Inject constructor(private val apiService: BudgetApiService) :
        AccountRepository {
    override fun create(newItem: Account): Single<Account> = apiService.newAccount(newItem)

    override fun findAll(): Single<Collection<Account>> = apiService.getAccounts()

    override fun findById(id: Long): Single<Account> = apiService.getAccount(id)

    override fun update(updatedItem: Account): Single<Account> =
            apiService.updateAccount(updatedItem.id!!, updatedItem)

    override fun delete(id: Long): Single<Void> = apiService.deleteAccount(id)

    override fun getBalance(id: Long): Single<Long> = Single.create {
        apiService.getAccountBalance(id).subscribe { res, err ->
            if (err != null) {
                it.onError(err)
            } else {
                it.onSuccess(res.balance)
            }
        }
    }
}
