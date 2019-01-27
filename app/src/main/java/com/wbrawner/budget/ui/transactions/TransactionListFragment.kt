package com.wbrawner.budget.ui.transactions

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.text.emoji.widget.EmojiTextView
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wbrawner.budget.R
import com.wbrawner.budget.data.model.TransactionWithCategory

class TransactionListFragment : Fragment() {
    lateinit var viewModel: TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity == null) {
            return
        }

        if (savedInstanceState != null) {
            return
        }

        viewModel = ViewModelProviders.of(activity!!).get(TransactionViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_transaction_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list_transactions)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add_transaction)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        viewModel.getTransactions(20)
                .observe(this, Observer<List<TransactionWithCategory>> { data ->
                    val noDataView = view.findViewById<EmojiTextView>(R.id.transaction_list_no_data)
                    if (data == null || data.isEmpty()) {
                        recyclerView.adapter = null
                        noDataView?.setText(R.string.transactions_no_data)
                        recyclerView?.visibility = View.GONE
                        noDataView?.visibility = View.VISIBLE
                    } else {
                        recyclerView.adapter = TransactionAdapter(data)
                        recyclerView.visibility = View.VISIBLE
                        noDataView.visibility = View.GONE
                    }
                })
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) fab.hide() else fab.show()
            }
        })
        fab.setOnClickListener {
            startActivity(
                    Intent(activity, AddEditTransactionActivity::class.java)
            )
        }

        return view
    }

    companion object {
        const val TAG_FRAGMENT = "transactions"
        const val TITLE_FRAGMENT = R.string.title_transactions
    }
}