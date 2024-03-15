package com.wbrawner.twigs.android.ui.util

import android.content.Context
import android.content.ContextWrapper
import android.text.format.DateFormat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.wbrawner.twigs.android.R
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.text.DateFormat.getDateTimeInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    modifier: Modifier,
    date: Instant,
    setDate: (Instant) -> Unit,
    label: String = "Date",
    dialogVisible: Boolean,
    setDialogVisible: (Boolean) -> Unit
) {
    Log.d("DatePicker", "date input: ${date.toEpochMilliseconds()}")
    val context = LocalContext.current
    OutlinedTextField(
        modifier = modifier
            .clickable {
                Log.d("DatePicker", "click!")
                setDialogVisible(true)
            }
            .focusRequester(FocusRequester())
            .onFocusChanged {
                setDialogVisible(it.hasFocus)
            },
        value = date.format(context),
        onValueChange = {},
        readOnly = true,
        label = {
            Text(label)
        }
    )
    val dialog = remember {
        val localTime = date.toLocalDateTime(TimeZone.UTC).time
        MaterialDatePicker.Builder.datePicker()
            .setSelection(date.toEpochMilliseconds())
            .setTheme(R.style.DateTimePickerDialogTheme)
            .build()
            .also { picker ->
                picker.addOnPositiveButtonClickListener {
                    setDate(
                        LocalDateTime(
                            Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date,
                            localTime
                        ).toInstant(TimeZone.UTC)
                    )
                }
                picker.addOnDismissListener {
                    setDialogVisible(false)
                }
            }
    }
    DisposableEffect(key1 = dialogVisible) {
        if (dialogVisible) {
            context.fragmentManager?.let {
                dialog.show(it, null)
            }
        } else if (dialog.isVisible) {
            dialog.dismiss()
        }
        onDispose {
            if (dialog.isVisible) {
                dialog.dismiss()
            }
        }
    }
}

val Context.activity: AppCompatActivity?
    get() = when (this) {
        is AppCompatActivity -> this
        is ContextWrapper -> baseContext.activity
        else -> null
    }

val Context.fragmentManager: FragmentManager?
    get() = this.activity?.supportFragmentManager

fun Instant.format(context: Context): String =
    DateFormat.getDateFormat(context)
        .format(
            this.toLocalDateTime(TimeZone.currentSystemDefault()).date.atStartOfDayIn(TimeZone.currentSystemDefault())
                .toEpochMilliseconds()
        )

fun Instant.formatWithTime(): String = getDateTimeInstance().format(this.toEpochMilliseconds())
