package com.wbrawner.budget.data.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.wbrawner.budget.data.model.Category
import com.wbrawner.budget.data.model.Transaction

@Dao
interface CategoryDao {
    @Insert(onConflict = REPLACE)
    fun save(category: Category)

    @Query("SELECT * FROM `Category` WHERE id = :id")
    fun load(id: Int): LiveData<Category>

    @Query("SELECT * FROM `Category`")
    fun loadMultiple(): LiveData<List<Category>>

    @Query("SELECT " +
            "(SELECT TOTAL(amount) from `Transaction` WHERE type = 'INCOME' AND categoryId = :categoryId) " +
            "- (SELECT TOTAL(amount) from `Transaction` WHERE type = 'EXPENSE' AND categoryId = :categoryId)")
    fun getBalanceForCategory(categoryId: Int): LiveData<Double>

    @Delete
    fun delete(category: Category)

    @Query("DELETE FROM `Category` WHERE id = :id")
    fun deleteById(id: Int)


    @Query("SELECT * FROM `Transaction` WHERE categoryId = :id")
    fun getTransactions(id: Int): LiveData<List<Transaction>>
}