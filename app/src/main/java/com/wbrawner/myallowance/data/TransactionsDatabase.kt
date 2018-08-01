package com.wbrawner.myallowance.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

@Database(entities = [(Transaction::class)], version = 1, exportSchema = false)
@TypeConverters(DateTypeConverter::class, TransactionTypeTypeConverter::class)
abstract class TransactionsDatabase: RoomDatabase() {
    abstract fun dao(): TransactionDao
}