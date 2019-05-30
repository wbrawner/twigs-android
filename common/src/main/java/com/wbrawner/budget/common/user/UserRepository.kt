package com.wbrawner.budget.common.user

import com.wbrawner.budget.common.Repository
import io.reactivex.Single

interface UserRepository : Repository<User, Long> {
    fun findAll(accountId: Long): Single<Collection<User>>
}