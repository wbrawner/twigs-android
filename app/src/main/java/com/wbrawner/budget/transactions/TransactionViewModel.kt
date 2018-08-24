package com.wbrawner.budget.transactions

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.data.Transaction
import com.wbrawner.budget.data.TransactionRepository
import com.wbrawner.budget.data.TransactionType

class TransactionViewModel(application: Application): AndroidViewModel(application) {
    private val transactionRepo = TransactionRepository((application as AllowanceApplication).dao)

    fun getTransaction(id: Int): LiveData<Transaction> = transactionRepo.getTransaction(id)

    fun getTransactions(count: Int): LiveData<List<Transaction>> = transactionRepo.getTransactions(count)

    fun getTransactionsByType(count: Int, type: TransactionType): LiveData<List<Transaction>>
            = transactionRepo.getTransactionsByType(count, type)

    fun getCurrentBalance(): LiveData<Double> = transactionRepo.getCurrentBalance()

    fun saveTransaction(transaction: Transaction) = transactionRepo.save(transaction)

    fun deleteTransaction(transaction: Transaction) = transactionRepo.delete(transaction)

    fun deleteTransactionById(id: Int) = transactionRepo.deleteById(id)
}