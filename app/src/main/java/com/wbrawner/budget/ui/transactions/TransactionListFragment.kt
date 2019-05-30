package com.wbrawner.budget.ui.transactions

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.emoji.widget.EmojiTextView
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.common.account.Account
import com.wbrawner.budget.di.BudgetViewModelFactory
import com.wbrawner.budget.ui.autoDispose
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TransactionListFragment : androidx.fragment.app.Fragment() {
    private val disposables = CompositeDisposable()
    @Inject
    lateinit var viewModelFactory: BudgetViewModelFactory
    lateinit var listViewModel: TransactionListViewModel
    lateinit var account: Account
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    lateinit var noDataView: EmojiTextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_transaction_list, container, false)
        recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.list_transactions)
        noDataView = view.findViewById<EmojiTextView>(R.id.transaction_list_no_data)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add_transaction)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        (activity!!.application as AllowanceApplication).appComponent.inject(this)
        listViewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(TransactionListViewModel::class.java)
        recyclerView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) fab.hide() else fab.show()
            }
        })
        fab.setOnClickListener {
            startActivity(
                    Intent(activity, AddEditTransactionActivity::class.java).apply {
                        putExtra(EXTRA_ACCOUNT_ID, account.id!!)
                    }
            )
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        listViewModel.getTransactions(account.id!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data ->
                    if (data.isEmpty()) {
                        recyclerView.adapter = null
                        noDataView.setText(R.string.transactions_no_data)
                        recyclerView.visibility = View.GONE
                        noDataView.visibility = View.VISIBLE
                    } else {
                        recyclerView.adapter = TransactionAdapter(activity!!, data.toList())
                        recyclerView.visibility = View.VISIBLE
                        noDataView.visibility = View.GONE
                    }
                }
                .autoDispose(disposables)
    }

    override fun onDestroyView() {
        disposables.dispose()
        super.onDestroyView()
    }

    companion object {
        const val TAG_FRAGMENT = "transactions"
        const val TITLE_FRAGMENT = R.string.title_transactions
    }
}
