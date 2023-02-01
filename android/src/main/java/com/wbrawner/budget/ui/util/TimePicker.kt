package com.wbrawner.budget.ui.util

import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import com.wbrawner.twigs.shared.recurringtransaction.Time
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    modifier: Modifier,
    date: Instant,
    setDate: (Instant) -> Unit,
    dialogVisible: Boolean,
    setDialogVisible: (Boolean) -> Unit
) {
    val context = LocalContext.current
    OutlinedTextField(
        modifier = modifier
            .clickable {
                setDialogVisible(true)
            }
            .focusRequester(FocusRequester())
            .onFocusChanged {
                setDialogVisible(it.hasFocus)
            },
        value = date.formatTime(context),
        onValueChange = {},
        readOnly = true,
        label = {
            Text("Time")
        }
    )
    val dialog = remember {
        val localTime = date.toLocalDateTime(TimeZone.currentSystemDefault())
        TimePickerDialog(
            context,
            { _, hour, minute ->
                setDate(
                    LocalDateTime(
                        localTime.date,
                        LocalTime(hour, minute)
                    ).toInstant(TimeZone.UTC)
                        .minus(java.util.TimeZone.getDefault().rawOffset.milliseconds)
                )
            },
            localTime.hour,
            localTime.minute,
            DateFormat.is24HourFormat(context)
        ).also { picker ->
            picker.setOnDismissListener {
                setDialogVisible(false)
            }
        }
    }
    DisposableEffect(key1 = dialogVisible) {
        if (dialogVisible) {
            context.fragmentManager?.let {
                dialog.show()
            }
        } else if (dialog.isShowing) {
            dialog.dismiss()
        }
        onDispose {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePicker(
    modifier: Modifier,
    time: Time,
    setTime: (Time) -> Unit,
    dialogVisible: Boolean,
    setDialogVisible: (Boolean) -> Unit
) {
    val context = LocalContext.current
    OutlinedTextField(
        modifier = modifier
            .clickable {
                setDialogVisible(true)
            }
            .focusRequester(FocusRequester())
            .onFocusChanged {
                setDialogVisible(it.hasFocus)
            },
        value = time.toString(),
        onValueChange = {},
        readOnly = true,
        label = {
            Text("Time")
        }
    )
    val dialog = remember {
        val localTime = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        TimePickerDialog(
            context,
            { _, hour, minute -> setTime(Time(hour, minute, 0)) },
            localTime.hour,
            localTime.minute,
            DateFormat.is24HourFormat(context)
        ).also { picker ->
            picker.setOnDismissListener {
                setDialogVisible(false)
            }
        }
    }
    DisposableEffect(key1 = dialogVisible) {
        if (dialogVisible) {
            context.fragmentManager?.let {
                dialog.show()
            }
        } else if (dialog.isShowing) {
            dialog.dismiss()
        }
        onDispose {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }
}

fun Instant.formatTime(context: Context): String =
    DateFormat.getTimeFormat(context).format(this.toEpochMilliseconds())
