package com.wbrawner.budget.data

import android.arch.lifecycle.LiveData
import android.os.Handler
import android.os.HandlerThread
import com.wbrawner.budget.data.dao.TransactionDao
import com.wbrawner.budget.data.model.Transaction
import com.wbrawner.budget.data.model.TransactionCategory
import com.wbrawner.budget.data.model.TransactionWithCategory

class TransactionRepository(private val dao: TransactionDao) {
    private val handler: Handler

    init {
        val thread = HandlerThread("transactions")
        thread.start()
        handler = Handler(thread.looper)
    }

//    fun getTransactionsByType(count: Int, type: TransactionType): LiveData<List<TransactionWithCategory>> =
//            dao.loadMultipleByType(count, type)

    fun getTransactions(count: Int): LiveData<List<TransactionWithCategory>> = dao.loadMultiple(count)


    fun getTransaction(id: Int): LiveData<TransactionWithCategory> = dao.load(id)


    fun save(transaction: Transaction) {
        handler.post { dao.save(transaction) }
    }


    fun delete(transaction: Transaction) {
        handler.post { dao.delete(transaction) }
    }


    fun deleteById(id: Int) {
        handler.post { dao.deleteById(id) }
    }


    fun getCurrentBalance(): LiveData<Int> = dao.getBalance()

    fun getCategories(): LiveData<List<TransactionCategory>> = dao.loadCategories()
}
