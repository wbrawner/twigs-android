package com.wbrawner.budget.ui.categories

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
import com.wbrawner.budget.ui.categories.AddEditCategoryActivity.Companion.EXTRA_ACCOUNT_ID
import com.wbrawner.budget.ui.hideFabOnScroll
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class CategoryListFragment : androidx.fragment.app.Fragment() {
    @Inject
    lateinit var viewModelFactory: BudgetViewModelFactory
    lateinit var viewModel: CategoryViewModel
    lateinit var account: Account
    private val disposables = CompositeDisposable()
    var recyclerView: androidx.recyclerview.widget.RecyclerView? = null
    var noDataView: EmojiTextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_transaction_list, container, false)
        recyclerView = view.findViewById(R.id.list_transactions)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add_transaction)
        recyclerView?.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        (activity!!.application as AllowanceApplication).appComponent.inject(this)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory).get(CategoryViewModel::class.java)
        noDataView = view.findViewById(R.id.transaction_list_no_data)
        recyclerView?.hideFabOnScroll(fab)
        fab.setOnClickListener {
            startActivity(Intent(activity, AddEditCategoryActivity::class.java).apply {
                putExtra(EXTRA_ACCOUNT_ID, account.id)
            })
        }
        return view
    }

    override fun onDestroyView() {
        disposables.dispose()
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCategories(account.id!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data ->
                    if (data.isEmpty()) {
                        recyclerView?.adapter = null
                        noDataView?.setText(R.string.categories_no_data)
                        recyclerView?.visibility = View.GONE
                        noDataView?.visibility = View.VISIBLE
                    } else {
                        recyclerView?.adapter = CategoryAdapter(activity!!, account, data.toList(),
                                viewModel)
                        recyclerView?.visibility = View.VISIBLE
                        noDataView?.visibility = View.GONE
                    }
                }
                .autoDispose(disposables)
    }

    companion object {
        const val TAG_FRAGMENT = "categories"
        const val TITLE_FRAGMENT = R.string.title_categories
    }
}
