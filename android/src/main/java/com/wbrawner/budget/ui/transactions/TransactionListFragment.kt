package com.wbrawner.budget.ui.transactions

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.wbrawner.budget.R
import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.ui.EXTRA_BUDGET_ID
import com.wbrawner.budget.ui.EXTRA_CATEGORY_ID
import com.wbrawner.budget.ui.EXTRA_TRANSACTION_ID
import com.wbrawner.budget.ui.base.BindableAdapter
import com.wbrawner.budget.ui.base.BindableData
import com.wbrawner.budget.ui.base.ListWithAddButtonFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class TransactionListFragment : ListWithAddButtonFragment<Transaction, TransactionListViewModel>() {
    override val noItemsStringRes: Int = R.string.transactions_no_data
    override val viewModel: TransactionListViewModel by viewModels()
    override val constructors: Map<Int, (View) -> BindableAdapter.BindableViewHolder<Transaction>>
        get() = mapOf(TRANSACTION_VIEW to { v -> TransactionViewHolder(v, findNavController()) })

    override fun reloadItems() {
        viewModel.getTransactions(categoryId = arguments?.getString(EXTRA_CATEGORY_ID))
    }

    override fun bindData(data: Transaction): BindableData<Transaction> = BindableData(data, TRANSACTION_VIEW)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_transaction_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId != R.id.filter) {
            return super.onOptionsItemSelected(item)
        }
        // TODO: Launch a Google Drive-style search/filter screen
        AlertDialog.Builder(requireContext(), R.style.DialogTheme)
            .setTitle("Filter Transactions")
            .setPositiveButton(R.string.action_submit) { _, _ ->
                reloadItems()
            }
            .setNegativeButton(R.string.action_cancel) { _, _ ->
                // Do nothing
            }
            .create()
            .show()
        return true
    }

    override fun addItem() {
        startActivity(Intent(activity, TransactionFormActivity::class.java))
    }

    companion object {
        const val TAG_FRAGMENT = "transactions"
    }
}

const val TRANSACTION_VIEW = R.layout.list_item_transaction

class TransactionViewHolder(itemView: View, val navController: NavController) : BindableAdapter.CoroutineViewHolder<Transaction>(itemView) {
    private val name: TextView = itemView.findViewById(R.id.transaction_title)
    private val description: TextView = itemView.findViewById(R.id.transaction_description)
    private val date: TextView = itemView.findViewById(R.id.transaction_date)
    private val amount: TextView = itemView.findViewById(R.id.transaction_amount)

    @SuppressLint("NewApi")
    override fun onBind(item: BindableData<Transaction>) {
        val transaction = item.data
        name.text = transaction.title
        if (transaction.description.isNullOrBlank()) {
            description.visibility = View.GONE
        } else {
            description.visibility = View.VISIBLE
            description.text = transaction.description
        }
        date.text = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(transaction.date)
        amount.text = String.format("${'$'}%.02f", transaction.amount / 100.0f)
        val context = itemView.context
        val color = if (transaction.expense) R.color.colorTextRed else R.color.colorTextGreen
        amount.setTextColor(ContextCompat.getColor(context, color))
        itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString(EXTRA_BUDGET_ID, transaction.budgetId)
                putString(EXTRA_TRANSACTION_ID, transaction.id)
            }
            navController.navigate(R.id.addEditTransactionActivity, bundle)
        }
    }
}