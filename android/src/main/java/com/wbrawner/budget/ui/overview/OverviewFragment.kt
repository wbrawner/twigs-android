package com.wbrawner.budget.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.di.BudgetViewModelFactory
import com.wbrawner.budget.ui.toAmountSpannable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class OverviewFragment : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Main
    @Inject
    lateinit var viewModelFactory: BudgetViewModelFactory
    lateinit var viewModel: AccountOverviewViewModel
    lateinit var budget: Budget
    private var inflatedView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_overview, container, false)
        (activity!!.application as AllowanceApplication).appComponent.inject(this)
        viewModel = ViewModelProviders.of(activity!!, viewModelFactory)
                .get(AccountOverviewViewModel::class.java)
        inflatedView = view
        return view
    }

    override fun onStart() {
        super.onStart()
        launch {
            val balance = viewModel.getBalance(budget.id!!)
            view?.findViewById<TextView>(R.id.overview_current_balance)
                    ?.text = balance.toAmountSpannable(view?.context)
            showData(inflatedView, true)
        }
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
