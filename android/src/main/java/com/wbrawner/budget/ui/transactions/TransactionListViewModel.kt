package com.wbrawner.budget.ui.transactions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.AsyncViewModel
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.common.transaction.TransactionRepository
import com.wbrawner.budget.launch
import javax.inject.Inject

class TransactionListViewModel : ViewModel(), AsyncViewModel<List<Transaction>> {
    @Inject
    lateinit var budgetRepository: BudgetRepository

    @Inject
    lateinit var transactionRepo: TransactionRepository
    override val state: MutableLiveData<AsyncState<List<Transaction>>> = MutableLiveData(AsyncState.Loading)

    fun getTransactions(
            categoryId: String? = null
    ) {
        budgetRepository.currentBudget.observeForever { budget ->
            val budgets = budget?.id?.let { listOf(it) }
            val categories = categoryId?.let { listOf(it) }
            launch {
                transactionRepo.findAll(budgets, categories).toList()
            }
        }

    }
}
