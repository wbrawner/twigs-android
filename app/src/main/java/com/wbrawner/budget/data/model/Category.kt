package com.wbrawner.budget.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.ColorInt

@Entity
class Category(
        @PrimaryKey
        val id: Int?,
        val name: String,
        val amount: Double,
        val repeat: String?,
        @ColorInt val color: Int
)