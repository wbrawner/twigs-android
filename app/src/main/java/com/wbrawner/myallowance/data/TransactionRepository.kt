package com.wbrawner.myallowance.data

import android.arch.lifecycle.LiveData
import android.os.Handler
import android.os.HandlerThread

class TransactionRepository(val dao: TransactionDao) {
    val handler: Handler
    val uiHandler: Handler = Handler()

    init {
        val thread = HandlerThread("transactions")
        thread.start()
        handler = Handler(thread.looper)
    }

    fun getTransactionsByType(count: Int, type: TransactionType): LiveData<List<Transaction>> =
            dao.loadMultipleByType(count, type)

    fun getTransactions(count: Int): LiveData<List<Transaction>> = dao.loadMultiple(count)

//    fun getTransactions(count: Int): LiveData<List<Transaction>> {
//        val data = MutableLiveData<List<Transaction>>()
//
//        handler.post {
//            val transactions = ArrayList<Transaction>()
//            for (i in 0..count) {
//                transactions.add(Transaction(
//                        i,
//                        "Transaction $i",
//                        Date(),
//                        "Spent some money on something",
//                        (Math.random() * 100).toFloat(),
//                        TransactionType.EXPENSE
//                ))
//            }
//
//            uiHandler.post {
//                data.value = transactions
//            }
//        }
//
//        return data
//    }

    fun getTransaction(id: Int): LiveData<Transaction> = dao.load(id)


    fun save(transaction: Transaction) {
        handler.post { dao.save(transaction) }
    }


    fun delete(transaction: Transaction) {
        handler.post { dao.delete(transaction) }
    }


    fun deleteById(id: Int) {
        handler.post { dao.deleteById(id) }
    }


    fun getCurrentBalance(): LiveData<Double> = dao.getBalance()
}