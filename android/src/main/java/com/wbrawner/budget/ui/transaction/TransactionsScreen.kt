package com.wbrawner.budget.ui.transaction

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wbrawner.budget.ui.TwigsScaffold
import com.wbrawner.budget.ui.base.TwigsApp
import com.wbrawner.budget.ui.util.format
import com.wbrawner.twigs.shared.Store
import com.wbrawner.twigs.shared.transaction.Transaction
import com.wbrawner.twigs.shared.transaction.TransactionAction
import com.wbrawner.twigs.shared.transaction.groupByDate
import kotlinx.datetime.Clock
import kotlinx.datetime.toInstant
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(store: Store) {
    TwigsScaffold(
        store = store,
        title = "Transactions",
        onClickFab = {
            store.dispatch(TransactionAction.NewTransactionClicked)
        }
    ) {
        val state by store.state.collectAsState()
        state.transactions?.let { transactions ->
            val transactionGroups = remember { transactions.groupByDate() }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(horizontal = 8.dp)
            ) {
                transactionGroups.forEach { (timestamp, transactions) ->
                    item(timestamp) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = timestamp.toInstant().format(LocalContext.current),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    item(transactions) {
                        Card {
                            transactions.forEach { transaction ->
                                TransactionListItem(transaction) {
                                    store.dispatch(TransactionAction.SelectTransaction(transaction.id))
                                }
                            }
                        }
                    }
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        if (state.editingTransaction) {
            TransactionFormDialog(store = store)
        }
    }
}

@Composable
fun TransactionListItem(transaction: Transaction, onClick: (Transaction) -> Unit) {
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
            color = if (transaction.expense) Color.Red else Color.Green,
        )
    }
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
fun TransactionListItem_Preview() {
    TwigsApp {
        TransactionListItem(
            transaction = Transaction(
                title = "Google Store",
                description = "Pixel 7 Pro",
                date = Clock.System.now(),
                amount = 129999,
                budgetId = "budgetId",
                expense = true,
                createdBy = "createdBy"
            )
        ) {}
    }
}

fun Long.toCurrencyString(): String =
    NumberFormat.getCurrencyInstance().format(this.toDouble() / 100.0)

fun Long.toDecimalString(): String = if (this > 0) (this.toDouble() / 100.0).toString() else ""