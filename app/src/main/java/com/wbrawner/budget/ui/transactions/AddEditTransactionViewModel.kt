package com.wbrawner.budget.ui.transactions

import androidx.lifecycle.ViewModel
import com.wbrawner.budget.common.account.AccountRepository
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.common.transaction.TransactionRepository
import com.wbrawner.budget.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject

class AddEditTransactionViewModel @Inject constructor(
        private val accountRepository: AccountRepository,
        private val categoryRepository: CategoryRepository,
        private val transactionRepository: TransactionRepository
) : ViewModel() {
    fun getCategories(accountId: Long) = categoryRepository.findAll(accountId)

    fun getTransaction(id: Long) = transactionRepository.findById(id)

    fun saveTransaction(transaction: Transaction) = if (transaction.id == null)
        transactionRepository.create(transaction)
    else
        transactionRepository.update(transaction)

    fun deleteTransaction(id: Long) = transactionRepository.delete(id)

    fun getAccount(id: Long) = accountRepository.findById(id)
}

@Module
abstract class AddEditTransactionViewModelMapper {
    @Binds
    @IntoMap
    @ViewModelKey(AddEditTransactionViewModel::class)
    abstract fun bindAddEditTransactionViewModel(addEditTransactionViewModel:
                                                 AddEditTransactionViewModel): ViewModel
}