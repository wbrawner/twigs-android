package com.wbrawner.budget.ui.transactions

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wbrawner.budget.R
import com.wbrawner.budget.ui.transactions.AddEditTransactionActivity.Companion.EXTRA_TRANSACTION_ID
import com.wbrawner.budget.data.model.Transaction
import com.wbrawner.budget.data.model.TransactionWithCategory
import java.text.SimpleDateFormat

class TransactionAdapter() : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    private lateinit var data: List<Any>
    private lateinit var listType: Class<Any>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction: Transaction =
                if (listType == TransactionWithCategory::class.java) (data[position] as TransactionWithCategory).transaction
                else data[position] as Transaction
        holder.title.text = transaction.name
        holder.date.text = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(transaction.date)
        holder.amount.text = String.format("${'$'}%.02f", transaction.amount / 100.0f)
        val context = holder.itemView.context
        val color = if (transaction.isExpense) R.color.colorTextRed else R.color.colorTextGreen
        holder.amount.setTextColor(context.resources.getColor(color, context.theme))
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

    constructor(transactions: List<Any>) : this() {
        if (transactions.isEmpty()) {
            return
        }

        listType = transactions.first().javaClass
        data = transactions
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.transaction_title)
        val date = itemView.findViewById<TextView>(R.id.transaction_date)
        val amount = itemView.findViewById<TextView>(R.id.transaction_amount)
    }
}
