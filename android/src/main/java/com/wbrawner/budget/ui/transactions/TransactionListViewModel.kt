package com.wbrawner.budget.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.AsyncViewModel
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.common.transaction.TransactionRepository
import com.wbrawner.budget.load
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    val budgetRepository: BudgetRepository,
    val transactionRepo: TransactionRepository,
) : ViewModel(), AsyncViewModel<List<Transaction>> {
    override val state: MutableStateFlow<AsyncState<List<Transaction>>> =
        MutableStateFlow(AsyncState.Loading)

    fun getTransactions(
        categoryId: String? = null,
    ) {
        viewModelScope.launch {
            budgetRepository.currentBudget.collect { budget ->
                val budgets = budget?.id?.let { listOf(it) }
                val categories = categoryId?.let { listOf(it) }
                load {
                    transactionRepo.findAll(budgets, categories).toList()
                }
            }
        }
    }
}
