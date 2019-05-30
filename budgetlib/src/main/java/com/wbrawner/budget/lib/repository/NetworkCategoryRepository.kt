package com.wbrawner.budget.lib.repository

import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.lib.network.BudgetApiService
import io.reactivex.Single
import javax.inject.Inject
class NetworkCategoryRepository @Inject constructor(private val apiService: BudgetApiService) : CategoryRepository {
    override fun create(newItem: Category): Single<Category> = apiService.newCategory(newItem)

    override fun findAll(accountId: Long): Single<Collection<Category>> = Single.create { subscriber ->
        apiService.getCategories(accountId).subscribe { categories, error ->
            if (error != null) {
                subscriber.onError(error)
            } else {
                subscriber.onSuccess(categories.sortedBy { it.title })
            }
        }

    }

    /**
     * This will only return an empty list, since an accountId is required to get categories.
     * Pass a [Long] as the first (and only) parameter to denote the
     * [account ID][com.wbrawner.budget.common.account.Account.id] instead
     */
    override fun findAll(): Single<Collection<Category>> = Single.just(ArrayList())

    override fun findById(id: Long): Single<Category> = apiService.getCategory(id)

    override fun update(updatedItem: Category): Single<Category> =
            apiService.updateCategory(updatedItem.id!!, updatedItem)

    override fun delete(id: Long): Single<Void> = apiService.deleteCategory(id)

    // TODO: Implement this method server-side and then here
    override fun getBalance(id: Long): Single<Long> = Single.create { subscriber ->
        apiService.getCategoryBalance(id).subscribe { res, err ->
            if (err != null) {
                subscriber.onError(err)
            } else {
                subscriber.onSuccess(res.balance)
            }
        }
    }
}

