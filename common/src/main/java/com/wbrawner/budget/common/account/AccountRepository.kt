package com.wbrawner.budget.common.account

import com.wbrawner.budget.common.Repository
import io.reactivex.Single

interface AccountRepository : Repository<Account, Long> {
    fun getBalance(id: Long): Single<Long>
}