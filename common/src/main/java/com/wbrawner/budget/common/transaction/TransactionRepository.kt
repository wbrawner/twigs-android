package com.wbrawner.budget.common.transaction

import com.wbrawner.budget.common.Repository
import io.reactivex.Single

interface TransactionRepository : Repository<Transaction, Long> {
    fun findAll(accountId: Long): Single<Collection<Transaction>>
}