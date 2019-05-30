package com.wbrawner.budget.ui.categories

import androidx.lifecycle.ViewModel
import com.wbrawner.budget.common.account.AccountRepository
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.reactivex.Single
import javax.inject.Inject

class CategoryViewModel @Inject constructor(private val accountRepo: AccountRepository, private val categoryRepo:
CategoryRepository) : ViewModel() {
    fun getAccount(id: Long) = accountRepo.findById(id)

    fun getCategory(id: Long): Single<Category> = categoryRepo.findById(id)

    fun getCategories(accountId: Long): Single<Collection<Category>> = categoryRepo.findAll(accountId)

    fun saveCategory(category: Category) = if (category.id == null) categoryRepo.create(category)
    else categoryRepo.update(category)

    fun deleteCategoryById(id: Long) = categoryRepo.delete(id)

    fun getBalance(id: Long) = categoryRepo.getBalance(id)
}

@Module
abstract class CategoryViewModelMapper {
    @Binds
    @IntoMap
    @ViewModelKey(CategoryViewModel::class)
    abstract fun bindCategoryViewModel(categoryViewModel: CategoryViewModel): ViewModel
}