package com.wbrawner.myallowance.transactions

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wbrawner.myallowance.R
import com.wbrawner.myallowance.data.Transaction

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
        holder.amount.text = String.format("${'$'}%.02f", transaction.amount)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.transaction_title)
        val amount = itemView.findViewById<TextView>(R.id.transaction_amount)
    }
}