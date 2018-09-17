package com.wbrawner.budget.data.migrations

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

class MIGRATION_1_2: Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE `Category` (`id` INTEGER, `name` TEXT, `amount` REAL, " +
                "`repeat` TEXT, `color` INTEGER PRIMARY KEY (`id`))")
        database.execSQL("ALTER TABLE `Transaction` ADD COLUMN categoryId")
    }

}