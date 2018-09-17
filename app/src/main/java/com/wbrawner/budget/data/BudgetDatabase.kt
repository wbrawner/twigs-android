package com.wbrawner.budget.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.wbrawner.budget.data.dao.CategoryDao
import com.wbrawner.budget.data.dao.TransactionDao
import com.wbrawner.budget.data.model.Category
import com.wbrawner.budget.data.model.Transaction

@Database(entities = [(Transaction::class), (Category::class)], version = 2)
@TypeConverters(DateTypeConverter::class, TransactionTypeTypeConverter::class)
abstract class BudgetDatabase: RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
}