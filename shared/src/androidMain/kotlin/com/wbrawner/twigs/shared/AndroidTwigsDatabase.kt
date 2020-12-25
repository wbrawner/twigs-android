package com.wbrawner.twigs.shared

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import com.wbrawner.twigs.TwigsDatabase

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(TwigsDatabase.Schema, context, "twigs.db")
    }
}
