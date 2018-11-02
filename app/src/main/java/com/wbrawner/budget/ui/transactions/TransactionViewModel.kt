package com.wbrawner.budget.ui.transactions

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.data.*
import com.wbrawner.budget.data.model.Transaction
import com.wbrawner.budget.data.model.TransactionCategory
import com.wbrawner.budget.data.model.TransactionWithCategory

class TransactionViewModel(application: Application): AndroidViewModel(application) {
    private val transactionRepo = TransactionRepository((application as AllowanceApplication).transactionDao)

    fun getTransaction(id: Int): LiveData<TransactionWithCategory> = transactionRepo.getTransaction(id)

    fun getTransactions(count: Int): LiveData<List<TransactionWithCategory>> = transactionRepo.getTransactions(count)

//    fun getTransactionsByType(count: Int, type: TransactionType): LiveData<List<TransactionWithCategory>>
//            = transactionRepo.getTransactionsByType(count, type)

    fun getCurrentBalance(): LiveData<Int> = transactionRepo.getCurrentBalance()

    fun getCategories(): LiveData<List<TransactionCategory>> = transactionRepo.getCategories()

    fun saveTransaction(transaction: Transaction) = transactionRepo.save(transaction)

    fun deleteTransaction(transaction: Transaction) = transactionRepo.delete(transaction)

    fun deleteTransactionById(id: Int) = transactionRepo.deleteById(id)
}
