package com.wbrawner.budget.ui

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wbrawner.budget.R
import java.text.NumberFormat
import java.util.*

fun RecyclerView.hideFabOnScroll(fab: FloatingActionButton) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0) fab.hide() else fab.show()
        }
    })
}

fun View.hide() {
    show(false)
}

fun View.show(show: Boolean = true) {
    visibility = if (show) View.VISIBLE else View.GONE
}

fun EditText.ensureNotEmpty(): Boolean {
    return if (this.text.isBlank()) {
        this.error = this.context.getString(R.string.error_required_field)
        false
    } else {
        this.error = null
        true
    }
}

fun Long.toAmountSpannable(context: Context? = null): Spannable {
    val currency = NumberFormat.getCurrencyInstance().apply {
        currency = Currency.getInstance(Locale.getDefault())
    }.format(this / 100.0f)
    val spannableStringBuilder = SpannableStringBuilder(currency)
    if (context == null) {
        return spannableStringBuilder
    }
    val color = when {
        this > 0 -> R.color.colorTextGreen
        this == 0L -> R.color.colorTextPrimary
        else -> R.color.colorTextRed
    }
    spannableStringBuilder.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, color)),
            0,
            spannableStringBuilder.length,
            0
    )
    return spannableStringBuilder
}