package com.wbrawner.budget.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.wbrawner.budget.data.TransactionType
import java.util.*

@Entity
class Transaction(
        @PrimaryKey
        val id: Int?,
        val title: String,
        val date: Date,
        val description: String,
        val amount: Double,
        val categoryId: Int?,
        val type: TransactionType
)