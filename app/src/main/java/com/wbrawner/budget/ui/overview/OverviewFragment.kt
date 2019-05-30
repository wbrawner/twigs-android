package com.wbrawner.budget.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.common.account.Account
import com.wbrawner.budget.di.BudgetViewModelFactory
import com.wbrawner.budget.ui.autoDispose
import com.wbrawner.budget.ui.fromBackgroundToMain
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class OverviewFragment : androidx.fragment.app.Fragment() {
    private val disposables = CompositeDisposable()
    @Inject
    lateinit var viewModelFactory: BudgetViewModelFactory
    lateinit var viewModel: AccountOverviewViewModel
    lateinit var account: Account
    private var inflatedView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_overview, container, false)
        (activity!!.application as AllowanceApplication).appComponent.inject(this)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)
                .get(AccountOverviewViewModel::class.java)
        inflatedView = view
        return view
    }

    override fun onResume() {
        super.onResume()
        viewModel.getBalance(account.id!!)
                .fromBackgroundToMain()
                .subscribe { balance ->
                    val balanceView = view!!.findViewById<TextView>(R.id.overview_current_balance)
                    val color = when {
                        balance > 0 -> R.color.colorTextGreen
                        balance == 0L -> R.color.colorTextPrimary
                        else -> R.color.colorTextRed
                    }
                    balanceView.setTextColor(ContextCompat.getColor(context!!, color))
                    balanceView.text = String.format("${'$'}%.02f", balance / 100.0f)
                    showData(inflatedView, true)
                }.autoDispose(disposables)
    }

    private fun showData(view: View?, show: Boolean) {
        if (view == null) return
        val dataVisibility = if (show) View.VISIBLE else View.GONE
        val noDataVisibility = if (show) View.GONE else View.VISIBLE
        view.findViewById<TextView>(R.id.overview_current_balance_label).visibility = dataVisibility
        view.findViewById<TextView>(R.id.overview_current_balance).visibility = dataVisibility
        view.findViewById<TextView>(R.id.overview_no_data).visibility = noDataVisibility
    }

    companion object {
        const val TAG_FRAGMENT = "overview"
        const val TITLE_FRAGMENT = R.string.app_name
    }
}
