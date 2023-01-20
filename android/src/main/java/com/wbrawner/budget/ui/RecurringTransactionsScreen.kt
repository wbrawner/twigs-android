package com.wbrawner.budget.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.wbrawner.twigs.shared.Store

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringTransactionsScreen(store: Store) {
    TwigsScaffold(store = store, title = "Recurring Transactions") {
        Text("Not yet implemented")
    }
}
