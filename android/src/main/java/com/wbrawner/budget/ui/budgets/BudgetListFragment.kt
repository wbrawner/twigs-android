package com.wbrawner.budget.ui.budgets


import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.ui.EXTRA_BUDGET_ID
import com.wbrawner.budget.ui.base.BindableAdapter
import com.wbrawner.budget.ui.base.BindableData
import com.wbrawner.budget.ui.base.ListWithAddButtonFragment

class BudgetListFragment : ListWithAddButtonFragment<Budget, BudgetListViewModel>() {
    override val noItemsStringRes: Int = R.string.overview_no_data

    override fun onAttach(context: Context) {
        (requireActivity().application as AllowanceApplication).appComponent.inject(viewModel)
        super.onAttach(context)
    }

    override val viewModel: BudgetListViewModel by viewModels()

    override fun reloadItems() {
        viewModel.getBudgets()
    }

    override fun bindData(data: Budget): BindableData<Budget> = BindableData(data, BUDGET_VIEW)

    override val constructors: Map<Int, (View) -> BindableAdapter.BindableViewHolder<Budget>>
        get() = mapOf(BUDGET_VIEW to { v -> BudgetViewHolder(v, findNavController()) })

    override fun addItem() {
        findNavController().navigate(R.id.addEditBudget)
    }
}

const val BUDGET_VIEW = R.layout.list_item_budget

class BudgetViewHolder(itemView: View, val navController: NavController) : BindableAdapter.BindableViewHolder<Budget>(itemView) {
    private val name: TextView = itemView.findViewById(R.id.budgetName)
    private val description: TextView = itemView.findViewById(R.id.budgetDescription)
//    private val balance: TextView = itemView.findViewById(R.id.budgetBalance)

    override fun onBind(item: BindableData<Budget>) {
        val budget = item.data
        name.text = budget.name
        if (budget.description.isNullOrBlank()) {
            description.visibility = View.GONE
        } else {
            description.visibility = View.VISIBLE
            description.text = budget.description
        }
        itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString(EXTRA_BUDGET_ID, budget.id)
            }
            navController.navigate(R.id.categoryListFragment, bundle)
        }
    }
}