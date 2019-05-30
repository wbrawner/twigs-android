package com.wbrawner.budget.common.category

import com.wbrawner.budget.common.Repository
import io.reactivex.Single

interface CategoryRepository : Repository<Category, Long> {
    fun findAll(accountId: Long): Single<Collection<Category>>
    fun getBalance(id: Long): Single<Long>
}