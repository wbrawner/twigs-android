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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wbrawner.budget.ui.TwigsScaffold
import com.wbrawner.budget.ui.base.TwigsApp
import com.wbrawner.budget.ui.util.format
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
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = transaction.date.format(LocalContext.current),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = transaction.amount.toCurrencyString(),
                style = MaterialTheme.typography.headlineSmall,
                color = if (transaction.expense) Color.Red else Color.Green
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