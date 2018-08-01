package com.wbrawner.myallowance.data

import android.support.annotation.StringRes
import com.wbrawner.myallowance.R
import java.io.Serializable

enum class TransactionType(@StringRes val title: Int, @StringRes val addTitle: Int) : Serializable {
    INCOME(R.string.title_income, R.string.title_add_income),
    EXPENSE(R.string.title_expenses, R.string.title_add_expense);
}
