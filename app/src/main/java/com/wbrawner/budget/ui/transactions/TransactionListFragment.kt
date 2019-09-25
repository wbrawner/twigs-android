package com.wbrawner.budget.ui.transactions

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.ui.EXTRA_BUDGET_ID
import com.wbrawner.budget.ui.EXTRA_CATEGORY_ID
import com.wbrawner.budget.ui.EXTRA_TRANSACTION_ID
import com.wbrawner.budget.ui.base.BindableAdapter
import com.wbrawner.budget.ui.base.BindableState
import com.wbrawner.budget.ui.base.ListWithAddButtonFragment
import java.text.SimpleDateFormat

class TransactionListFragment : ListWithAddButtonFragment<TransactionListViewModel>() {
    override val viewModelClass: Class<TransactionListViewModel> = TransactionListViewModel::class.java
    override val noItemsStringRes: Int = R.string.transactions_no_data

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as AllowanceApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun addItem() {
        startActivity(Intent(activity, AddEditTransactionActivity::class.java))
    }

    override suspend fun loadItems(): Pair<List<BindableState>, Map<Int, (view: View) -> BindableAdapter.BindableViewHolder<in BindableState>>> {
        return Pair(
                viewModel.getTransactions(
                        arguments?.getLong(EXTRA_BUDGET_ID),
                        arguments?.getLong(EXTRA_CATEGORY_ID)
                ).map { TransactionState(it) },
                mapOf(TRANSACTION_VIEW to { v ->
                    TransactionViewHolder(v, findNavController()) as BindableAdapter.BindableViewHolder<in BindableState>
                })
        )
    }

    companion object {
        const val TAG_FRAGMENT = "transactions"
    }
}

const val TRANSACTION_VIEW = R.layout.list_item_transaction

class TransactionState(val transaction: Transaction) : BindableState {
    override val viewType: Int = TRANSACTION_VIEW
}

class TransactionViewHolder(
        itemView: View,
        private val navController: NavController
) : BindableAdapter.CoroutineViewHolder<TransactionState>(itemView) {
    private val name: TextView = itemView.findViewById(R.id.transaction_title)
    private val date: TextView = itemView.findViewById(R.id.transaction_date)
    private val amount: TextView = itemView.findViewById(R.id.transaction_amount)

    @SuppressLint("NewApi")
    override fun onBind(item: TransactionState) {
        with(item) {
            name.text = transaction.title
            date.text = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).format(transaction.date)
            amount.text = String.format("${'$'}%.02f", transaction.amount / 100.0f)
            val context = itemView.context
            val color = if (transaction.expense) R.color.colorTextRed else R.color.colorTextGreen
            amount.setTextColor(ContextCompat.getColor(context, color))
            itemView.setOnClickListener {
                val bundle = Bundle().apply {
                    putLong(EXTRA_BUDGET_ID, transaction.budgetId)
                    putLong(EXTRA_TRANSACTION_ID, transaction.id ?: -1)
                }
                navController.navigate(R.id.addEditTransactionActivity, bundle)
            }
        }
    }
}