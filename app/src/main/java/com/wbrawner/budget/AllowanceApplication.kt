package com.wbrawner.budget

import android.app.Application
import android.arch.persistence.room.Room
import com.wbrawner.budget.data.BudgetDatabase
import com.wbrawner.budget.data.dao.CategoryDao
import com.wbrawner.budget.data.dao.TransactionDao
import com.wbrawner.budget.data.migrations.MIGRATION_1_2
import com.wbrawner.budget.data.migrations.MIGRATION_2_3

class AllowanceApplication : Application() {
    lateinit var database: BudgetDatabase
        private set

    lateinit var transactionDao: TransactionDao
        private set

    lateinit var categoryDao: CategoryDao
        private set

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(applicationContext, BudgetDatabase::class.java, "transactions")
                .addMigrations(MIGRATION_1_2())
                .addMigrations(MIGRATION_2_3())
                .build()
        transactionDao = database.transactionDao()
        categoryDao = database.categoryDao()
    }
}
