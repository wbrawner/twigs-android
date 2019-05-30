package com.wbrawner.budget.ui.transactions

import androidx.lifecycle.ViewModel
import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.common.transaction.TransactionRepository
import com.wbrawner.budget.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import io.reactivex.Single
import javax.inject.Inject

class TransactionListViewModel @Inject constructor(private val transactionRepo: TransactionRepository) :
        ViewModel
        () {
    fun getTransactions(accountId: Long): Single<Collection<Transaction>> =
            transactionRepo.findAll(accountId)
}

@Module
abstract class TransactionListViewModelMapper {
    @Binds
    @IntoMap
    @ViewModelKey(TransactionListViewModel::class)
    abstract fun bindTransactionListViewModel(transactionListViewModel:
                                              TransactionListViewModel): ViewModel
}