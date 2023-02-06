package com.wbrawner.budget.ui.util

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.InputChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.wbrawner.twigs.shared.recurringtransaction.DayOfMonth
import com.wbrawner.twigs.shared.recurringtransaction.DayOfYear
import com.wbrawner.twigs.shared.recurringtransaction.Frequency
import com.wbrawner.twigs.shared.recurringtransaction.Ordinal
import com.wbrawner.twigs.shared.recurringtransaction.capitalizedName
import com.wbrawner.twigs.shared.recurringtransaction.toMonth
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrequencyPicker(frequency: Frequency, setFrequency: (Frequency) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = frequency.count.toString(),
            onValueChange = { setFrequency(frequency.update(count = it.toInt())) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            label = { Text("Repeat Every") },
        )

        val (unitExpanded, setUnitExpanded) = remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            modifier = Modifier
                .fillMaxWidth(),
            expanded = unitExpanded,
            onExpandedChange = setUnitExpanded,
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                value = frequency.name,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Time Unit")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded)
                }
            )
            ExposedDropdownMenu(expanded = unitExpanded, onDismissRequest = {
                setUnitExpanded(false)
            }) {
                DropdownMenuItem(
                    text = { Text("Daily") },
                    onClick = {
                        setFrequency(Frequency.Daily(frequency.count, frequency.time))
                        setUnitExpanded(false)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Weekly") },
                    onClick = {
                        setFrequency(Frequency.Weekly(frequency.count, setOf(), frequency.time))
                        setUnitExpanded(false)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Monthly") },
                    onClick = {
                        setFrequency(
                            Frequency.Monthly(
                                frequency.count,
                                DayOfMonth.FixedDayOfMonth(
                                    Clock.System.now().toLocalDateTime(
                                        TimeZone.UTC
                                    ).dayOfMonth
                                ),
                                frequency.time
                            )
                        )
                        setUnitExpanded(false)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Yearly") },
                    onClick = {
                        val today = Clock.System.now().toLocalDateTime(
                            TimeZone.UTC
                        )
                        setFrequency(
                            Frequency.Yearly(
                                frequency.count,
                                DayOfYear.of(today.monthNumber, today.dayOfMonth),
                                frequency.time
                            )
                        )
                        setUnitExpanded(false)
                    }
                )
            }
        }
    }

    when (frequency) {
        is Frequency.Daily -> {
            // No additional config needed
        }

        is Frequency.Weekly -> {
            WeeklyFrequencyPicker(frequency, setFrequency)
        }

        is Frequency.Monthly -> {
            MonthlyFrequencyPicker(frequency, setFrequency)
        }

        is Frequency.Yearly -> {
            YearlyFrequencyPicker(frequency, setFrequency)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyFrequencyPicker(frequency: Frequency.Weekly, setFrequency: (Frequency) -> Unit) {
    val daysOfWeek = remember { DayOfWeek.values() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        daysOfWeek.forEach {
            val label = remember(it) { it.getDisplayName(TextStyle.SHORT, Locale.getDefault()) }
            InputChip(
                selected = frequency.daysOfWeek.contains(it),
                onClick = {
                    val selection = frequency.daysOfWeek.toMutableSet()
                    if (selection.contains(it)) {
                        selection.remove(it)
                    } else {
                        selection.add(it)
                    }
                    setFrequency(frequency.copy(daysOfWeek = selection))
                },
                label = { Text(label) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyFrequencyPicker(frequency: Frequency.Monthly, setFrequency: (Frequency) -> Unit) {
    val (fixedDay, setFixedDay) = remember {
        mutableStateOf(
            (frequency.dayOfMonth as? DayOfMonth.FixedDayOfMonth)?.day ?: 1
        )
    }
    val (ordinal, setOrdinal) = remember { mutableStateOf((frequency.dayOfMonth as? DayOfMonth.OrdinalDayOfMonth)?.ordinal) }
    val (dayOfWeek, setDayOfWeek) = remember {
        mutableStateOf(
            (frequency.dayOfMonth as? DayOfMonth.OrdinalDayOfMonth)?.dayOfWeek ?: DayOfWeek.SUNDAY
        )
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.weight(1f)) {
            val (ordinalExpanded, setOrdinalExpanded) = remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth(),
                expanded = ordinalExpanded,
                onExpandedChange = setOrdinalExpanded,
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = ordinal?.capitalizedName ?: "Day",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = ordinalExpanded)
                    }
                )
                ExposedDropdownMenu(expanded = ordinalExpanded, onDismissRequest = {
                    setOrdinalExpanded(false)
                }) {
                    DropdownMenuItem(
                        text = { Text("Day") },
                        onClick = {
                            setOrdinal(null)
                            setFrequency(
                                frequency.copy(
                                    dayOfMonth = DayOfMonth.FixedDayOfMonth(
                                        fixedDay
                                    )
                                )
                            )
                            setOrdinalExpanded(false)
                        }
                    )
                    Ordinal.values().forEach { ordinal ->
                        DropdownMenuItem(
                            text = { Text(ordinal.capitalizedName) },
                            onClick = {
                                setOrdinal(ordinal)
                                setFrequency(
                                    frequency.copy(
                                        dayOfMonth = DayOfMonth.OrdinalDayOfMonth(
                                            ordinal,
                                            dayOfWeek
                                        )
                                    )
                                )
                                setOrdinalExpanded(false)
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            val (dayExpanded, setDayExpanded) = remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth(),
                expanded = dayExpanded,
                onExpandedChange = setDayExpanded,
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = ordinal?.let { dayOfWeek.capitalizedName } ?: fixedDay.toString(),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded)
                    }
                )
                ExposedDropdownMenu(expanded = dayExpanded, onDismissRequest = {
                    setDayExpanded(false)
                }) {
                    if (ordinal == null) {
                        for (day in 1..31) {
                            DropdownMenuItem(
                                text = { Text(day.toString()) },
                                onClick = {
                                    setFixedDay(day)
                                    setFrequency(
                                        frequency.copy(
                                            dayOfMonth = DayOfMonth.FixedDayOfMonth(
                                                day
                                            )
                                        )
                                    )
                                    setDayExpanded(false)
                                }
                            )
                        }
                    } else {
                        DayOfWeek.values().forEach { dayOfWeek ->
                            DropdownMenuItem(
                                text = { Text(dayOfWeek.capitalizedName) },
                                onClick = {
                                    setDayOfWeek(dayOfWeek)
                                    setFrequency(
                                        frequency.copy(
                                            dayOfMonth = (frequency.dayOfMonth as DayOfMonth.OrdinalDayOfMonth).copy(
                                                dayOfWeek = dayOfWeek
                                            )
                                        )
                                    )
                                    setDayExpanded(false)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearlyFrequencyPicker(frequency: Frequency.Yearly, setFrequency: (Frequency) -> Unit) {
    val (month, setMonth) = remember { mutableStateOf(frequency.dayOfYear.month) }
    val (day, setDay) = remember { mutableStateOf(frequency.dayOfYear.day) }
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.weight(1f)) {
            val (monthExpanded, setMonthExpanded) = remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth(),
                expanded = monthExpanded,
                onExpandedChange = setMonthExpanded,
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExpanded)
                    }
                )
                ExposedDropdownMenu(expanded = monthExpanded, onDismissRequest = {
                    setMonthExpanded(false)
                }) {
                    Month.values().forEach { m ->
                        DropdownMenuItem(
                            text = { Text(m.getDisplayName(TextStyle.FULL, Locale.getDefault())) },
                            onClick = {
                                setMonth(m.value)
                                setFrequency(
                                    frequency.copy(
                                        dayOfYear = DayOfYear.of(m.value, min(day, m.maxLength()))
                                    )
                                )
                                setMonthExpanded(false)
                            }
                        )
                    }
                }
            }
        }
        Box(modifier = Modifier.weight(1f)) {
            val (dayExpanded, setDayExpanded) = remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                modifier = Modifier
                    .fillMaxWidth(),
                expanded = dayExpanded,
                onExpandedChange = setDayExpanded,
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = day.toString(),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayExpanded)
                    }
                )
                ExposedDropdownMenu(expanded = dayExpanded, onDismissRequest = {
                    setDayExpanded(false)
                }) {
                    for (d in 1..month.toMonth().maxLength()) {
                        DropdownMenuItem(
                            text = { Text(d.toString()) },
                            onClick = {
                                setDay(d)
                                setFrequency(frequency.copy(dayOfYear = DayOfYear.of(month, d)))
                                setDayExpanded(false)
                            }
                        )
                    }
                }
            }
        }
    }
}