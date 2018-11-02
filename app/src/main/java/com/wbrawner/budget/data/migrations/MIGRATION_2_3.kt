package com.wbrawner.budget.data.migrations

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration

class MIGRATION_2_3: Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `Transaction` RENAME TO `TransactionOld`")
        database.execSQL("CREATE TABLE `Transaction` (`id` INTEGER,`remoteId` TEXT,`name` TEXT NOT NULL,`date` TEXT NOT NULL, `description` TEXT NOT NULL, `amount` INTEGER NOT NULL,`isExpense` INTEGER NOT NULL DEFAULT 1,`categoryId` INTEGER,PRIMARY KEY (`id`))")
        database.execSQL("INSERT INTO `Transaction` ( id, name, date, description, amount, categoryId ) SELECT id,title,date,description,amount * 100,categoryId FROM `TransactionOld`")
        database.execSQL("UPDATE `Transaction` SET isExpense = 1 WHERE id IN (SELECT id FROM `TransactionOld` WHERE `TransactionOld`.type = 'EXPENSE')")
        database.execSQL("UPDATE `Transaction` SET isExpense = 0 WHERE id IN (SELECT id FROM `TransactionOld` WHERE `TransactionOld`.type = 'INCOME')")
        database.execSQL("DROP TABLE `TransactionOld`")

        database.execSQL("ALTER TABLE `Category` RENAME TO `CategoryOld`")
        database.execSQL("CREATE TABLE `Category` (`id` INTEGER,`remoteId` TEXT,`name` TEXT NOT NULL, `amount` INTEGER NOT NULL, `repeat` TEXT, `color` INTEGER NOT NULL DEFAULT 0,PRIMARY KEY (`id`))")
        database.execSQL("INSERT INTO `Category` ( id, name, amount, repeat, color ) SELECT id,name,amount * 100,repeat,color FROM `CategoryOld`")
        database.execSQL("DROP TABLE `CategoryOld`")
    }
}
