package com.wbrawner.twigs.shared

import com.squareup.sqldelight.db.SqlDriver
import com.wbrawner.twigs.TwigsDatabase

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(TwigsDatabase.Schema, "twigs.db")
    }
}
