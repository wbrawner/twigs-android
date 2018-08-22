package com.wbrawner.myallowance

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.wbrawner.myallowance.data.TransactionDao
import com.wbrawner.myallowance.data.TransactionsDatabase
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


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        ACRA.init(this)
    }
}