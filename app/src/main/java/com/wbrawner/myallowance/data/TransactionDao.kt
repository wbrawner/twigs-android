package com.wbrawner.myallowance.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

@Dao
interface TransactionDao {
    @Insert(onConflict = REPLACE)
    fun save(transaction: Transaction)

    @Query("SELECT * FROM `Transaction` WHERE id = :id")
    fun load(id: Int): LiveData<Transaction>

    @Query("SELECT * FROM `Transaction` LIMIT :count")
    fun loadMultiple(count: Int): LiveData<List<Transaction>>

    @Query("SELECT * FROM `Transaction` WHERE type = :type LIMIT :count")
    fun loadMultipleByType(count: Int, type: TransactionType): LiveData<List<Transaction>>

    @Query("SELECT (SELECT TOTAL(amount) from `Transaction` WHERE type = 'INCOME') - (SELECT TOTAL(amount) from `Transaction` WHERE type = 'EXPENSE')")
    fun getBalance(): LiveData<Double>

    @Delete
    fun delete(transaction: Transaction)

    @Query("DELETE FROM `Transaction` WHERE id = :id")
    fun deleteById(id: Int)
}