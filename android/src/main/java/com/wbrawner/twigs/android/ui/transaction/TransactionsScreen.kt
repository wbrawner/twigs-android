package com.wbrawner.twigs.android.ui.transaction

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wbrawner.twigs.android.ui.TwigsScaffold
import com.wbrawner.twigs.android.ui.base.TwigsApp
import com.wbrawner.twigs.android.ui.util.format
import com.wbrawner.twigs.shared.Store
import com.wbrawner.twigs.shared.transaction.Transaction
import com.wbrawner.twigs.shared.transaction.TransactionAction
import com.wbrawner.twigs.shared.transaction.groupByDate
import kotlinx.datetime.Clock
import kotlinx.datetime.toInstant
import java.text.NumberFormat

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionsScreen(store: Store) {
    val state by store.state.collectAsState()
    val budget = state.selectedBudget?.let { id -> state.budgets?.first { it.id == id } }
    TwigsScaffold(
        store = store,
        title = budget?.name ?: "Select a Budget",
        onClickFab = {
            store.dispatch(TransactionAction.NewTransactionClicked)
        }
    ) {
        state.transactions?.let { transactions ->
            val transactionGroups =
                remember(state.editingTransaction) { transactions.groupByDate() }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            ) {
                transactionGroups.forEach { (timestamp, transactions) ->
                    item {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = timestamp.toInstant().format(LocalContext.current),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    itemsIndexed(transactions) { index, transaction ->
                        TransactionListItem(
                            modifier = Modifier.animateItemPlacement(),
                            transaction,
                            index == 0,
                            index == transactions.lastIndex
                        ) {
                            store.dispatch(TransactionAction.SelectTransaction(transaction.id))
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
fun TransactionListItem(
    modifier: Modifier = Modifier,
    transaction: Transaction,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: (Transaction) -> Unit
) {
    val top = if (isFirst) MaterialTheme.shapes.medium.topStart else CornerSize(0.dp)
    val bottom = if (isLast) MaterialTheme.shapes.medium.bottomStart else CornerSize(0.dp)
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium.copy(
                    topStart = top,
                    topEnd = top,
                    bottomStart = bottom,
                    bottomEnd = bottom
                )
            )
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
            ),
            isFirst = true,
            isLast = true
        ) {}
    }
}

fun Long.toCurrencyString(): String =
    NumberFormat.getCurrencyInstance().format(this.toDouble() / 100.0)

fun Long.toDecimalString(): String = if (this > 0) {
    val decimal = (this.toDouble() / 100.0).toString()
    if (decimal.length - decimal.lastIndexOf('.') == 2) {
        decimal + '0'
    } else {
        decimal
    }
} else {
    ""
}