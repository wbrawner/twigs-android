package com.wbrawner.budget.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wbrawner.budget.ui.transaction.toCurrencyString
import com.wbrawner.twigs.shared.Store

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(store: Store) {
    val state by store.state.collectAsState()
    TwigsScaffold(store = store, title = "Overview") { padding ->
        val budget = state.selectedBudget?.let { id -> state.budgets?.first { it.id == id } }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            budget?.let { budget ->
                Text(budget.name)
                Text(budget.description ?: "")
            }
            Text("Cash Flow")
            Text(state.budgetBalance?.toCurrencyString() ?: "-")
        }
    }
}

