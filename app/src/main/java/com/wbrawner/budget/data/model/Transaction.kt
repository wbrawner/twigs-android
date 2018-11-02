package com.wbrawner.budget.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
class Transaction(
        @PrimaryKey
        val id: Int?,
        val remoteId: String?,
        val name: String,
        val date: Date,
        val description: String,
        val amount: Int,
        val categoryId: Int?,
        val isExpense: Boolean
)
