package com.wbrawner.budget.data.migrations

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

class MIGRATION_1_2: Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE `Category` (`id` INTEGER, `name` TEXT NOT NULL, `amount` REAL NOT NULL, `repeat` TEXT, `color` INTEGER NOT NULL DEFAULT 0, PRIMARY KEY (`id`))")
        database.execSQL("ALTER TABLE `Transaction` ADD COLUMN categoryId INTEGER")
    }

}
