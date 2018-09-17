package com.wbrawner.budget.data

import android.arch.lifecycle.LiveData
import android.os.Handler
import android.os.HandlerThread
import com.wbrawner.budget.data.dao.CategoryDao
import com.wbrawner.budget.data.model.Category
import com.wbrawner.budget.data.model.Transaction

class CategoryRepository(private val dao: CategoryDao) {
    private val handler: Handler

    init {
        val thread = HandlerThread("category")
        thread.start()
        handler = Handler(thread.looper)
    }

    fun getCategories(): LiveData<List<Category>> = dao.loadMultiple()


    fun getCategory(id: Int): LiveData<Category> = dao.load(id)


    fun save(category: Category) {
        handler.post { dao.save(category) }
    }


    fun delete(category: Category) {
        handler.post { dao.delete(category) }
    }


    fun deleteById(id: Int) {
        handler.post { dao.deleteById(id) }
    }


    fun getCurrentBalance(id: Int): LiveData<Double> = dao.getBalanceForCategory(id)


    fun getTransactions(id: Int): LiveData<List<Transaction>> = dao.getTransactions(id)
}