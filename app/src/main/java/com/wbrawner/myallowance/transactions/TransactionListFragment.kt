package com.wbrawner.myallowance.transactions

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wbrawner.myallowance.R
import com.wbrawner.myallowance.addedittransaction.AddEditTransactionActivity
import com.wbrawner.myallowance.addedittransaction.AddEditTransactionActivity.Companion.EXTRA_TYPE
import com.wbrawner.myallowance.data.Transaction
import com.wbrawner.myallowance.data.TransactionType
import java.io.Serializable

class TransactionListFragment : Fragment() {
    lateinit var viewModel: TransactionViewModel
    lateinit var type: TransactionType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (activity == null) {
            return
        }

        if (savedInstanceState != null) {
            return
        }

        type = arguments?.getSerializable(ARG_TYPE) as? TransactionType ?: TransactionType.EXPENSE

        viewModel = ViewModelProviders.of(activity!!).get(TransactionViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_transaction_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.list_transactions)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        viewModel.getTransactionsByType(20, type)
                .observe(this, Observer<List<Transaction>> { data ->
                    if (data == null) {
                        recyclerView.adapter = null
                    } else {
                        recyclerView.adapter = TransactionAdapter(data)
                    }
                })
        view.findViewById<FloatingActionButton>(R.id.fab_add_transaction).setOnClickListener {
            startActivity(
                    Intent(activity, AddEditTransactionActivity::class.java).apply {
                        this.putExtra(EXTRA_TYPE, this@TransactionListFragment.type.name)
                    }
            )
        }

        return view
    }

    companion object {
        const val ARG_TYPE = "TRANSACTION_TYPE"
    }
}