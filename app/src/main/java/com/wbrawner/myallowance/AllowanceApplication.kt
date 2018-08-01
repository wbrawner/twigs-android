package com.wbrawner.myallowance

import android.app.Application
import android.arch.lifecycle.ViewModelProvider
import android.arch.persistence.room.Room
import android.arch.persistence.room.Transaction
import com.wbrawner.myallowance.data.TransactionDao
import com.wbrawner.myallowance.data.TransactionsDatabase

class AllowanceApplication: Application() {
    lateinit var database: TransactionsDatabase
    private set

    lateinit var dao: TransactionDao
    private set

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(applicationContext, TransactionsDatabase::class.java, "transactions")
                .build()
        dao = database.dao()

    }
}