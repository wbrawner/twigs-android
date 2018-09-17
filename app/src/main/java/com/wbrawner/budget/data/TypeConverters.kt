package com.wbrawner.budget.data

import android.arch.persistence.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class TransactionTypeTypeConverter {
    @android.arch.persistence.room.TypeConverter
    fun toTransactionType(value: String): TransactionType? = TransactionType.valueOf(value)

    @android.arch.persistence.room.TypeConverter
    fun toString(type: TransactionType?): String? = type?.name
}

class DateTypeConverter {
    @android.arch.persistence.room.TypeConverter
    fun toDate(value: String): Date? = dateFormat.parse(value)

    @android.arch.persistence.room.TypeConverter
    fun toString(date: Date): String? = dateFormat.format(date)

    companion object {
        @JvmStatic
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }
}
