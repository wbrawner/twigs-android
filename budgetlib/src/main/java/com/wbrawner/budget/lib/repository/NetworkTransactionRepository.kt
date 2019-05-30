package com.wbrawner.budget.lib.repository

import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.common.transaction.TransactionRepository
import com.wbrawner.budget.lib.network.BudgetApiService
import io.reactivex.Single
import javax.inject.Inject

class NetworkTransactionRepository @Inject constructor(private val apiService: BudgetApiService) : TransactionRepository {
    override fun create(newItem: Transaction): Single<Transaction> = apiService.newTransaction(newItem)

    override fun findAll(accountId: Long): Single<Collection<Transaction>> = Single.create { subscriber ->
        apiService.getTransactions(accountId).subscribe { transactions, error ->
            if (error != null) {
                subscriber.onError(error)
            } else {
                subscriber.onSuccess(transactions.sortedByDescending { it.date })
            }
        }
    }

    /**
     * This will only return an empty list, since an accountId is required to get transactions.
     * Pass a [Long] as the first (and only) parameter to denote the
     * [account ID][com.wbrawner.budget.common.account.Account.id] instead
     */
    override fun findAll(): Single<Collection<Transaction>> = Single.just(ArrayList())

    override fun findById(id: Long): Single<Transaction> = apiService.getTransaction(id)

    override fun update(updatedItem: Transaction): Single<Transaction> =
            apiService.updateTransaction(updatedItem.id!!, updatedItem)

    override fun delete(id: Long): Single<Void> = apiService.deleteTransaction(id)
}
