package com.wbrawner.budget.data

import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import com.wbrawner.budget.R
import java.io.Serializable

enum class TransactionType(
        @StringRes val title: Int,
        @StringRes val addTitle: Int,
        @StringRes val editTitle: Int,
        @StringRes val noDataText: Int,
        @ColorRes val textColor: Int
) : Serializable {
    INCOME(
            R.string.title_income,
            R.string.title_add_income,
            R.string.title_edit_income,
            R.string.income_no_data,
            R.color.colorTextGreen
    ),
    EXPENSE(
            R.string.title_expenses,
            R.string.title_add_expense,
            R.string.title_edit_expense,
            R.string.expenses_no_data,
            R.color.colorTextRed
    );
}
