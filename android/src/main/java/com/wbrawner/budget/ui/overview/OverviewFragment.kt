package com.wbrawner.budget.ui.overview

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.R
import com.wbrawner.budget.ui.toAmountSpannable
import kotlinx.android.synthetic.main.fragment_overview.*

class OverviewFragment : Fragment() {
    val viewModel: OverviewViewModel by viewModels()

    override fun onAttach(context: Context) {
        (requireActivity().application as AllowanceApplication).appComponent.inject(viewModel)
        super.onAttach(context)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_overview, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        expectedActualChart.apply {
            setFitBars(true)
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "Income" else "Expenses"
                }

                override fun getBarLabel(barEntry: BarEntry?): String {
                    return super.getBarLabel(barEntry)
                }
            }
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.isGranularityEnabled = true
            setDrawGridBackground(false)
            axisLeft.axisMinimum = 0f
            axisRight.axisMinimum = 0f
        }
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
                    val expectedIncome = (state.data.expectedIncome / 100).toFloat()
                    val expectedExpenses = (state.data.expectedExpenses / 100).toFloat()
                    val actualIncome = (state.data.actualIncome / 100).toFloat()
                    val actualExpenses = (state.data.actualExpenses / 100).toFloat()
                    val max = maxOf(expectedIncome, expectedExpenses, actualIncome, actualExpenses)
                    expectedActualChart.axisLeft.axisMaximum = max
                    expectedActualChart.axisRight.axisMaximum = max
                    expectedActualChart.data = BarData(
                            BarDataSet(
                                    listOf(BarEntry(0f, expectedIncome), BarEntry(1f, expectedExpenses)),
                                    "Expected"
                            ).apply {
                                color = ResourcesCompat.getColor(resources, R.color.colorSecondary, requireContext().theme)
                            },
                            BarDataSet(
                                    listOf(BarEntry(0f, actualIncome), BarEntry(1f, actualExpenses)),
                                    "Actual"
                            ).apply {
                                color = ResourcesCompat.getColor(resources, R.color.colorAccent, requireContext().theme)
                            }
                    ).apply {
                        barWidth = 0.25f
                    }
                    expectedActualChart.groupBars(0f, 0.06f, 0.01f)
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
        viewModel.loadOverview(viewLifecycleOwner)
    }

    companion object {
        const val EXTRA_BUDGET_ID = "budgetId"
    }
}
