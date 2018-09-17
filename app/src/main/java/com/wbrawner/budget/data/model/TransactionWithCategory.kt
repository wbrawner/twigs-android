package com.wbrawner.budget.data.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import com.wbrawner.budget.data.model.Category
import com.wbrawner.budget.data.model.Transaction

class TransactionWithCategory {
        @Embedded
        lateinit var transaction: Transaction

        @Relation(parentColumn = "id", entityColumn = "id")
        lateinit var categorySet: Set<Category>
}