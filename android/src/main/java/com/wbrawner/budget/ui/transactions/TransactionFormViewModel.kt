package com.wbrawner.budget.ui.transactions

import androidx.lifecycle.ViewModel
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.common.transaction.TransactionRepository
import javax.inject.Inject

class TransactionFormViewModel : ViewModel() {
    @Inject
    lateinit var budgetRepository: BudgetRepository
    @Inject
    lateinit var categoryRepository: CategoryRepository
    @Inject
    lateinit var transactionRepository: TransactionRepository

    suspend fun getCategories(budgetId: Long, expense: Boolean) = categoryRepository.findAll(arrayOf(budgetId)).filter {
        it.expense == expense
    }

    suspend fun getTransaction(id: Long) = transactionRepository.findById(id)

    suspend fun saveTransaction(transaction: Transaction) = if (transaction.id == null)
        transactionRepository.create(transaction)
    else
        transactionRepository.update(transaction)

    suspend fun deleteTransaction(id: Long) = transactionRepository.delete(id)

    suspend fun getAccounts() = budgetRepository.findAll()
}
