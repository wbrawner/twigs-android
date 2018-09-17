package com.wbrawner.budget

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.wbrawner.budget.data.dao.TransactionDao
import com.wbrawner.budget.data.BudgetDatabase
import com.wbrawner.budget.data.dao.CategoryDao
import com.wbrawner.budget.data.migrations.MIGRATION_1_2
import org.acra.ACRA
import org.acra.annotation.AcraCore
import org.acra.annotation.AcraHttpSender
import org.acra.data.StringFormat
import org.acra.sender.HttpSender

@AcraCore(reportFormat = StringFormat.JSON)
@AcraHttpSender(uri = BuildConfig.ACRA_URL,
        basicAuthLogin = BuildConfig.ACRA_USER,
        basicAuthPassword = BuildConfig.ACRA_PASS,
        httpMethod = HttpSender.Method.POST)
class AllowanceApplication: Application() {
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
                .build()
        transactionDao = database.transactionDao()
        categoryDao = database.categoryDao()
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        if (!BuildConfig.DEBUG) ACRA.init(this)
    }
}
