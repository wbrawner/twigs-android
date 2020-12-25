package com.wbrawner.twigs.shared

import com.squareup.sqldelight.db.SqlDriver
import com.wbrawner.twigs.TwigsDatabase

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): TwigsDatabase {
    val driver = driverFactory.createDriver()
    val database = TwigsDatabase(driver)

    // Do more work with the database (see below).
    return database
}
