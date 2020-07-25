package com.wbrawner.budget;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.database.Cursor;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.wbrawner.budget.data.BudgetDatabase;
import com.wbrawner.budget.data.migrations.MIGRATION_1_2;
import com.wbrawner.budget.data.migrations.MIGRATION_2_3;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MigrationTests {
    private static final String TEST_DB = "migration-test";

    @Rule
    public MigrationTestHelper migrationHelper;

    public MigrationTests() {
        this.migrationHelper = new MigrationTestHelper(
                InstrumentationRegistry.getInstrumentation(),
                BudgetDatabase.class.getCanonicalName(),
                new FrameworkSQLiteOpenHelperFactory()
        );
    }

    @Test
    public void migrate1to2() throws IOException {
        SupportSQLiteDatabase db = migrationHelper.createDatabase(TEST_DB, 1);
        db.execSQL("INSERT INTO `Transaction` (id,title,date,description,amount,type) " +
                "VALUES (1,'An expense','2018-10-31','Spent some money',12.34,'EXPENSE')");
        db.close();
        db = migrationHelper.runMigrationsAndValidate(
                TEST_DB,
                2,
                true,
                new MIGRATION_1_2()
        );
        Cursor cursor = db.query("SELECT * FROM 'Transaction'");
        assertEquals(1, cursor.getCount());
        cursor.moveToFirst();
        assertEquals(1, cursor.getInt(cursor.getColumnIndex("id")));
        assertEquals("An expense", cursor.getString(cursor.getColumnIndex("title")));
        assertEquals("2018-10-31", cursor.getString(cursor.getColumnIndex("date")));
        assertEquals("Spent some money", cursor.getString(cursor.getColumnIndex("description")));
        assertEquals(12.34, cursor.getDouble(cursor.getColumnIndex("amount")));
        assertEquals(0, cursor.getInt(cursor.getColumnIndex("categoryId")));
        assertEquals("EXPENSE", cursor.getString(cursor.getColumnIndex("type")));
    }

    @Test
    public void migrate2to3() throws IOException {
        SupportSQLiteDatabase db = migrationHelper.createDatabase(TEST_DB, 2);
        db.execSQL("INSERT INTO `Transaction` (id,title,date,description,amount,categoryId,type) " +
                "VALUES (1,'An expense','2018-10-31','Spent some money',12.34,1,'EXPENSE')");
        db.execSQL("INSERT INTO `Transaction` (id,title,date,description,amount,categoryId,type) " +
                "VALUES (2,'Some income','2018-01-02','Made some money',42.65,0,'INCOME')");
        db.execSQL("INSERT INTO `Category` (id,title,amount,repeat,color) " +
                "VALUES (1,'Groceries',1234.56,'monthly',987)");
        db.close();
        db = migrationHelper.runMigrationsAndValidate(
                TEST_DB,
                3,
                true,
                new MIGRATION_2_3()
        );
        Cursor cursor = db.query("SELECT * FROM 'Transaction'");
        assertEquals(2, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals(1, cursor.getInt(cursor.getColumnIndex("id")));
        assertNull(cursor.getString(cursor.getColumnIndex("remoteId")));
        assertEquals("An expense", cursor.getString(cursor.getColumnIndex("title")));
        assertEquals("2018-10-31", cursor.getString(cursor.getColumnIndex("date")));
        assertEquals("Spent some money", cursor.getString(cursor.getColumnIndex("description")));
        assertEquals(1234, cursor.getInt(cursor.getColumnIndex("amount")));
        assertEquals(1, cursor.getInt(cursor.getColumnIndex("categoryId")));
        assertEquals(1, cursor.getInt(cursor.getColumnIndex("expense")));
        assertTrue(cursor.moveToNext());
        assertEquals(2, cursor.getInt(cursor.getColumnIndex("id")));
        assertNull(cursor.getString(cursor.getColumnIndex("remoteId")));
        assertEquals("Some income", cursor.getString(cursor.getColumnIndex("title")));
        assertEquals("2018-01-02", cursor.getString(cursor.getColumnIndex("date")));
        assertEquals("Made some money", cursor.getString(cursor.getColumnIndex("description")));
        assertEquals(4265, cursor.getInt(cursor.getColumnIndex("amount")));
        assertEquals(0, cursor.getInt(cursor.getColumnIndex("categoryId")));
        assertEquals(0, cursor.getInt(cursor.getColumnIndex("expense")));
        cursor = db.query("SELECT * FROM 'Category'");
        assertEquals(1, cursor.getCount());
        assertTrue(cursor.moveToFirst());
        assertEquals(1, cursor.getInt(cursor.getColumnIndex("id")));
        assertNull(cursor.getString(cursor.getColumnIndex("remoteId")));
        assertEquals("Groceries", cursor.getString(cursor.getColumnIndex("title")));
        assertEquals(123456, cursor.getInt(cursor.getColumnIndex("amount")));
        assertEquals("monthly", cursor.getString(cursor.getColumnIndex("repeat")));
        assertEquals(987, cursor.getInt(cursor.getColumnIndex("color")));
    }
}
