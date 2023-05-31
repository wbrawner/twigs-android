package com.wbrawner.budget.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wbrawner.budget.ui.base.TwigsColors
import com.wbrawner.budget.ui.base.TwigsTheme
import com.wbrawner.budget.ui.transaction.toCurrencyString
import com.wbrawner.twigs.shared.Store
import com.wbrawner.twigs.shared.budget.BudgetAction
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

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
                    val month =
                        remember(state.from) { state.from.toLocalDateTime(TimeZone.UTC).month }
                    val year =
                        remember(state.from) { state.from.toLocalDateTime(TimeZone.UTC).year }
                    var showMonthPicker by remember { mutableStateOf(false) }
                    LabeledField(
                        modifier = Modifier.clickable {
                            showMonthPicker = true
                        },
                        label = "Month",
                        value = "${
                            month.getDisplayName(
                                TextStyle.FULL,
                                LocalConfiguration.current.locales[0]
                            )
                        } $year"
                    )
                    LabeledField(
                        label = "Cash Flow",
                        value = state.budgetBalance?.toCurrencyString() ?: "-"
                    )
                    LabeledField(
                        label = "Transactions",
                        value = state.transactions?.size?.toString() ?: "-"
                    )
                    if (showMonthPicker) {
                        var monthField by remember { mutableStateOf(month) }
                        var yearField by remember { mutableStateOf(year.toString()) }
                        var yearError by remember { mutableStateOf(false) }
                        AlertDialog(
                            onDismissRequest = { showMonthPicker = false },
                            confirmButton = {
                                TextButton({
                                    if (!yearError) {
                                        showMonthPicker = false
                                        store.dispatch(
                                            BudgetAction.SetDateRange(
                                                monthField,
                                                yearField.toInt()
                                            )
                                        )
                                    }
                                }) {
                                    Text("Change")
                                }
                            },
                            dismissButton = {
                                TextButton({
                                    showMonthPicker = false
                                }) {
                                    Text("Cancel")
                                }
                            },
                            title = {
                                Text("Select a month to view")
                            },
                            text = {
                                val (monthExpanded, setMonthExpanded) = remember {
                                    mutableStateOf(false)
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = spacedBy(8.dp)
                                ) {
                                    ExposedDropdownMenuBox(
                                        modifier = Modifier
                                            .weight(1f),
                                        expanded = monthExpanded,
                                        onExpandedChange = setMonthExpanded,
                                    ) {
                                        OutlinedTextField(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(),
                                            value = month.getDisplayName(
                                                TextStyle.FULL,
                                                Locale.getDefault()
                                            ),
                                            label = {
                                                Text("Month")
                                            },
                                            onValueChange = {},
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExpanded)
                                            }
                                        )
                                        ExposedDropdownMenu(
                                            expanded = monthExpanded,
                                            onDismissRequest = {
                                                setMonthExpanded(false)
                                            }) {
                                            Month.values().forEach { m ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            m.getDisplayName(
                                                                TextStyle.FULL,
                                                                Locale.getDefault()
                                                            )
                                                        )
                                                    },
                                                    onClick = {
                                                        monthField = m
                                                        setMonthExpanded(false)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    OutlinedTextField(
                                        modifier = Modifier.weight(1f),
                                        value = yearField,
                                        onValueChange = { value ->
                                            yearField = value
                                            value.toIntOrNull()?.let {
                                                yearError = false
                                            } ?: run {
                                                yearError = true
                                            }
                                        },
                                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                        label = {
                                            Text("Year")
                                        },
                                        isError = yearError,
                                        supportingText = {
                                            if (yearError) {
                                                Text(
                                                    "Invalid year",
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        )
                    }
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
fun LabeledField(modifier: Modifier = Modifier, label: String, value: String) {
    Column(
        modifier = modifier,
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
