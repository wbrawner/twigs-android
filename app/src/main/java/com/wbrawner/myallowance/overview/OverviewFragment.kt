package com.wbrawner.myallowance.overview

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.wbrawner.myallowance.R
import com.wbrawner.myallowance.transactions.TransactionViewModel

class OverviewFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_overview, container, false)
        viewModel.getTransactions(1).observe(this, Observer { data ->
            if (data == null || data.isEmpty()) {
                showData(view, false)
                return@Observer
            }

            viewModel.getCurrentBalance().observe(this, Observer { balance ->
                val safeBalance: Double = balance?: 0.0
                val balanceView = view.findViewById<TextView>(R.id.overview_current_balance)
                val color = when {
                    safeBalance > 0.0 -> R.color.colorTextGreen
                    safeBalance == 0.0 -> R.color.colorTextPrimary
                    else -> R.color.colorTextRed
                }
                balanceView.setTextColor(resources.getColor(color, activity!!.theme))
                balanceView.text = String.format("${'$'}%.02f", safeBalance)
            })
            showData(view, true)
        })
        return view
    }

    private fun showData(view: View, show: Boolean) {
        val dataVisibility = if (show) View.VISIBLE else View.GONE
        val noDataVisibility =  if (show) View.GONE else View.VISIBLE
        view.findViewById<TextView>(R.id.overview_current_balance_label).visibility = dataVisibility
        view.findViewById<TextView>(R.id.overview_current_balance).visibility = dataVisibility
        view.findViewById<TextView>(R.id.overview_no_data).visibility = noDataVisibility
    }

    companion object {
        const val TAG_FRAGMENT = "overview"
        const val TITLE_FRAGMENT = R.string.app_name
    }
}