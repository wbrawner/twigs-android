package com.wbrawner.budget.ui.transactions

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.wbrawner.budget.R
import com.wbrawner.budget.common.transaction.Transaction
import java.text.SimpleDateFormat

class TransactionAdapter(private val activity: Activity, private val data: List<Transaction>)
    : androidx.recyclerview.widget.RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction: Transaction = data[position]
        holder.title?.text = transaction.title
        holder.date?.text = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(transaction.date)
        holder.amount?.text = String.format("${'$'}%.02f", transaction.amount / 100.0f)
        val context = holder.itemView.context
        val color = if (transaction.expense) R.color.colorTextRed else R.color.colorTextGreen
        holder.amount?.setTextColor(ContextCompat.getColor(context, color))
        holder.itemView.setOnClickListener {
            startActivity(
                    activity,
                    Intent(it.context.applicationContext, AddEditTransactionActivity::class.java)
                            .apply {
                                putExtra(EXTRA_ACCOUNT_ID, transaction.accountId)
                                putExtra(EXTRA_TRANSACTION_ID, transaction.id)
                            },
                    null
            )
        }
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val title: TextView? = itemView.findViewById(R.id.transaction_title)
        val date: TextView? = itemView.findViewById(R.id.transaction_date)
        val amount: TextView? = itemView.findViewById(R.id.transaction_amount)
    }
}
