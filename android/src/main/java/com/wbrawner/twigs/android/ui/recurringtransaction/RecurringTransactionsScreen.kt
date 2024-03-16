package com.wbrawner.twigs.android.ui.recurringtransaction

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wbrawner.twigs.android.ui.TwigsScaffold
import com.wbrawner.twigs.android.ui.base.TwigsApp
import com.wbrawner.twigs.android.ui.transaction.toCurrencyString
import com.wbrawner.twigs.shared.Store
import com.wbrawner.twigs.shared.recurringtransaction.Frequency
import com.wbrawner.twigs.shared.recurringtransaction.RecurringTransaction
import com.wbrawner.twigs.shared.recurringtransaction.RecurringTransactionAction
import com.wbrawner.twigs.shared.recurringtransaction.groupByStatus
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringTransactionsScreen(store: Store) {
    val state by store.state.collectAsState()
    val budget = state.selectedBudget?.let { id -> state.budgets?.first { it.id == id } }
    TwigsScaffold(
        store = store,
        title = budget?.name ?: "Select a Budget",
        onClickFab = {
            store.dispatch(RecurringTransactionAction.NewRecurringTransactionClicked)
        }
    ) {
        state.recurringTransactions?.let { transactions ->
            val transactionGroups =
                remember(state.editingRecurringTransaction) { transactions.groupByStatus() }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            ) {
                transactionGroups.forEach { (title, transactions) ->
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Card {
                        transactions.forEach { transaction ->
                            RecurringTransactionListItem(transaction) {
                                store.dispatch(
                                    RecurringTransactionAction.SelectRecurringTransaction(
                                        transaction.id
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        if (state.editingRecurringTransaction) {
            RecurringTransactionFormDialog(store = store)
        }
    }
}

@Composable
fun RecurringTransactionListItem(
    transaction: RecurringTransaction,
    onClick: (RecurringTransaction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(transaction) }
            .padding(8.dp)
            .heightIn(min = 56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = spacedBy(4.dp)
        ) {
            Text(transaction.title, style = MaterialTheme.typography.bodyLarge)
            if (!transaction.description.isNullOrBlank()) {
                Text(
                    transaction.description!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            transaction.amount.toCurrencyString(),
            color = if (transaction.expense) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
fun RecurringTransactionListItem_Preview() {
    TwigsApp {
        RecurringTransactionListItem(
            transaction = RecurringTransaction(
                title = "Google Store",
                description = "Pixel 7 Pro",
                frequency = Frequency.parse("Y;1;12-31;12:00:00"),
                start = Clock.System.now(),
                amount = 129999,
                budgetId = "budgetId",
                expense = true,
                createdBy = "createdBy"
            )
        ) {}
    }
}