package com.wbrawner.budget.ui.overview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.R
import com.wbrawner.budget.ui.toAmountSpannable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_overview.*

@AndroidEntryPoint
class OverviewFragment : Fragment() {
    val viewModel: OverviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_overview, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is AsyncState.Loading -> {
                    overviewContent.visibility = View.GONE
                    noData.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                }
                is AsyncState.Success -> {
                    overviewContent.visibility = View.VISIBLE
                    noData.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    activity?.title = state.data.budget.name
                    balance.text = state.data.balance.toAmountSpannable(view.context)
                }
                is AsyncState.Error -> {
                    overviewContent.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    noData.visibility = View.VISIBLE
                    Log.e("OverviewFragment", "Failed to load overview", state.exception)
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadOverview()
    }

    companion object {
        const val EXTRA_BUDGET_ID = "budgetId"
    }
}
