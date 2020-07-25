package com.wbrawner.budget.ui.overview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wbrawner.budget.R
import com.wbrawner.budget.common.budget.Budget

class AccountsAdapter() : RecyclerView.Adapter<AccountsAdapter.AccountViewHolder>() {
    private val accounts = mutableListOf<Budget>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AccountViewHolder(
            LayoutInflater.from(parent.context!!).inflate(R.layout.list_item_budget, parent, false)
    )

    override fun getItemCount(): Int = accounts.size

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accounts[holder.adapterPosition]
        holder.title.text = account.name
        holder.balance.text = account.name
    }

    class AccountViewHolder(
            itemView: View,
            val title: TextView = itemView.findViewById(R.id.budgetName),
            val balance: TextView = itemView.findViewById(R.id.budgetBalance)
    ) : RecyclerView.ViewHolder(itemView)
}