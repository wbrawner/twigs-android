package com.wbrawner.budget.transactions

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wbrawner.budget.R
import com.wbrawner.budget.addedittransaction.AddEditTransactionActivity
import com.wbrawner.budget.addedittransaction.AddEditTransactionActivity.Companion.EXTRA_TRANSACTION_ID
import com.wbrawner.budget.data.Transaction
import java.text.SimpleDateFormat

class TransactionAdapter(private val data: List<Transaction>)
    : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = data[position]
        holder.title.text = transaction.title
        holder.date.text = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(transaction.date)
        holder.amount.text = String.format("${'$'}%.02f", transaction.amount)
        val context = holder.itemView.context
        holder.amount.setTextColor(
                context.resources.getColor(transaction.type.textColor, context.theme)
        )
        holder.itemView.setOnClickListener {
            startActivity(
                    it.context.applicationContext,
                    Intent(it.context.applicationContext, AddEditTransactionActivity::class.java)
                            .apply {
                                putExtra(EXTRA_TRANSACTION_ID, transaction.id)
                            },
                    null
            )
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.transaction_title)
        val date = itemView.findViewById<TextView>(R.id.transaction_date)
        val amount = itemView.findViewById<TextView>(R.id.transaction_amount)
    }
}