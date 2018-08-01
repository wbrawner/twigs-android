package com.wbrawner.myallowance.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
class Transaction(
        @PrimaryKey
        val id: Int?,
        val title: String,
        val date: Date,
        val description: String,
        val amount: Double,
        val type: TransactionType
)