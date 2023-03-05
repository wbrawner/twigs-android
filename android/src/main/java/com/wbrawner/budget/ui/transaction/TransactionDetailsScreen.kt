package com.wbrawner.budget.ui.transaction

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wbrawner.budget.ui.TwigsScaffold
import com.wbrawner.budget.ui.base.TwigsApp
import com.wbrawner.budget.ui.util.formatWithTime
import com.wbrawner.twigs.shared.Action
import com.wbrawner.twigs.shared.Store
import com.wbrawner.twigs.shared.budget.Budget
import com.wbrawner.twigs.shared.category.Category
import com.wbrawner.twigs.shared.transaction.Transaction
import com.wbrawner.twigs.shared.transaction.TransactionAction
import com.wbrawner.twigs.shared.user.User
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsScreen(store: Store) {
    val state by store.state.collectAsState()
    val transaction =
        remember(state.editingTransaction) { state.transactions!!.first { it.id == state.selectedTransaction } }
    val createdBy = state.selectedTransactionCreatedBy ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val category = state.categories?.firstOrNull { it.id == transaction.categoryId }
    val budget = state.budgets!!.first { it.id == transaction.budgetId }
    val (confirmDeletionShown, setConfirmDeletionShown) = remember { mutableStateOf(false) }

    TwigsScaffold(
        store = store,
        title = "Transaction Details",
        navigationIcon = {
            IconButton(onClick = { store.dispatch(Action.Back) }) {
                Icon(Icons.Default.ArrowBack, "Go back")
            }
        },
        actions = {
            IconButton({ store.dispatch(TransactionAction.EditTransaction(requireNotNull(transaction.id))) }) {
                Icon(Icons.Default.Edit, "Edit")
            }
            IconButton({ setConfirmDeletionShown(true) }) {
                Icon(Icons.Default.Delete, "Delete")
            }
        }
    ) { padding ->
        TransactionDetails(
            modifier = Modifier.padding(padding),
            transaction = transaction,
            category = category,
            budget = budget,
            createdBy = createdBy
        )
        if (state.editingTransaction) {
            TransactionFormDialog(store = store)
        }
        if (confirmDeletionShown) {
            AlertDialog(
                text = {
                    Text("Are you sure you want to delete this transaction?")
                },
                onDismissRequest = { setConfirmDeletionShown(false) },
                confirmButton = {
                    TextButton(onClick = {
                        setConfirmDeletionShown(false)
                        store.dispatch(
                            TransactionAction.DeleteTransaction(
                                requireNotNull(
                                    transaction.id
                                )
                            )
                        )
                    }) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        setConfirmDeletionShown(false)
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetails(
    modifier: Modifier = Modifier,
    transaction: Transaction,
    category: Category? = null,
    budget: Budget,
    createdBy: User
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .scrollable(scrollState, Orientation.Vertical)
            .padding(16.dp),
        verticalArrangement = spacedBy(16.dp)
    ) {
        Text(
            text = transaction.title,
            style = MaterialTheme.typography.headlineMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = transaction.date.formatWithTime(),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = transaction.amount.toCurrencyString(),
                style = MaterialTheme.typography.headlineSmall,
                color = if (transaction.expense) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
        LabeledField("Description", transaction.description ?: "")
        LabeledField("Category", category?.title ?: "")
        LabeledField("Created By", createdBy.username)
    }
}

@Composable
fun LabeledField(label: String, field: String) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = spacedBy(4.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        Text(text = field, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
fun TransactionDetails_Preview() {
    TwigsApp {
        TransactionDetails(
            transaction = Transaction(
                title = "DAZBOG",
                description = "Chokolat Cappuccino",
                date = Clock.System.now(),
                amount = 550,
                categoryId = "coffee",
                budgetId = "budget",
                createdBy = "user",
                expense = true
            ),
            category = Category(title = "Coffee", budgetId = "budget", amount = 1000),
            budget = Budget(name = "Monthly Budget"),
            createdBy = User(username = "user")
        )
    }
}