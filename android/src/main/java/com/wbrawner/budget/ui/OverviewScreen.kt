package com.wbrawner.budget.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wbrawner.budget.ui.base.TwigsColors
import com.wbrawner.budget.ui.base.TwigsTheme
import com.wbrawner.budget.ui.transaction.toCurrencyString
import com.wbrawner.twigs.shared.Store

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(store: Store) {
    val state by store.state.collectAsState()
    val budget = state.selectedBudget?.let { id -> state.budgets?.first { it.id == id } }
    TwigsScaffold(store = store, title = budget?.name ?: "Select a Budget") { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
                .padding(8.dp),
            verticalArrangement = spacedBy(8.dp, alignment = Alignment.Top)
        ) {
            budget?.description?.let { description ->
                if (description.isNotBlank()) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalArrangement = spacedBy(8.dp)
                        ) {
                            Text(description, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LabeledField(
                        label = "Cash Flow",
                        value = state.budgetBalance?.toCurrencyString() ?: "-"
                    )
                    LabeledField(
                        label = "Transactions",
                        value = state.transactions?.size?.toString() ?: "-"
                    )
                }
            }
            CashFlowChart(
                expectedIncome = state.expectedIncome,
                actualIncome = state.actualIncome,
                expectedExpenses = state.expectedExpenses,
                actualExpenses = state.actualExpenses
            )
        }
    }
}

@Composable
fun CashFlowChart(
    expectedIncome: Long?,
    actualIncome: Long?,
    expectedExpenses: Long?,
    actualExpenses: Long?,
) {
    val maxValue = listOfNotNull(expectedIncome, expectedExpenses, actualIncome, actualExpenses)
        .maxOrNull()
        ?.toFloat()
        ?: 0f
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = spacedBy(8.dp)
        ) {
            CashFlowProgressBar(
                label = "Expected Income",
                value = expectedIncome,
                maxValue = maxValue,
                color = TwigsColors.DarkGreen,
                trackColor = MaterialTheme.colorScheme.outline
            )
            CashFlowProgressBar(
                label = "Actual Income",
                value = actualIncome,
                maxValue = maxValue,
                color = TwigsColors.Green,
                trackColor = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(4.dp))
            CashFlowProgressBar(
                label = "Expected Expenses",
                value = expectedExpenses,
                maxValue = maxValue,
                color = TwigsColors.DarkRed,
                trackColor = MaterialTheme.colorScheme.outline
            )
            CashFlowProgressBar(
                label = "Actual Expenses",
                value = actualExpenses,
                maxValue = maxValue,
                color = TwigsColors.Red,
                trackColor = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun CashFlowProgressBar(
    label: String,
    value: Long?,
    maxValue: Float,
    color: Color,
    trackColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label)
            Text(value?.toCurrencyString() ?: "-")
        }
        value?.let {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                progress = it.toFloat() / maxValue,
                color = color,
                trackColor = trackColor,
            )
        } ?: LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = trackColor,
        )
    }
}

@Composable
fun LabeledField(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = spacedBy(4.dp)
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CashFlowChart_Preview() {
    TwigsTheme {
        CashFlowChart(
            expectedIncome = 100,
            actualIncome = 50,
            expectedExpenses = 80,
            actualExpenses = 95
        )
    }
}

@Preview
@Composable
fun LabeledField_Preview() {
    TwigsTheme {
        LabeledField(
            label = "Transactions",
            value = "250"
        )
    }
}
