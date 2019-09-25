package com.wbrawner.budget.ui.budgets


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.ui.EXTRA_BUDGET_ID
import com.wbrawner.budget.ui.base.BindableAdapter
import com.wbrawner.budget.ui.base.BindableState
import com.wbrawner.budget.ui.base.ListWithAddButtonFragment
import kotlinx.coroutines.CoroutineScope

class BudgetListFragment : ListWithAddButtonFragment<BudgetViewModel>(), CoroutineScope {
    override val noItemsStringRes: Int = R.string.overview_no_data
    override val viewModelClass: Class<BudgetViewModel> = BudgetViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as AllowanceApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override suspend fun loadItems(): Pair<List<BindableState>, Map<Int, (view: View) -> BindableAdapter.BindableViewHolder<in BindableState>>> {
        val budgetItems = viewModel.getBudgets().map { BudgetState(it) }
        val navController = findNavController()
        return Pair(
                budgetItems,
                mapOf(BUDGET_VIEW to { v -> BudgetViewHolder(v, navController) as BindableAdapter.BindableViewHolder<in BindableState> })
        )
    }

    override fun addItem() {
        findNavController().navigate(R.id.addEditBudget)
    }
}

const val BUDGET_VIEW = R.layout.list_item_budget

class BudgetState(val budget: Budget) : BindableState {
    override val viewType: Int = BUDGET_VIEW
}

class BudgetViewHolder(
        itemView: View,
        private val navController: NavController
) : BindableAdapter.BindableViewHolder<BudgetState>(itemView) {
    private val name: TextView = itemView.findViewById(R.id.budgetName)
    private val description: TextView = itemView.findViewById(R.id.budgetDescription)
//    private val balance: TextView = itemView.findViewById(R.id.budgetBalance)

    override fun onBind(item: BudgetState) {
        with(item) {
            name.text = budget.name
            if (budget.description.isNullOrBlank()) {
                description.visibility = View.GONE
            } else {
                description.visibility = View.VISIBLE
                description.text = budget.description
            }
            itemView.setOnClickListener {
                val bundle = Bundle().apply {
                    putLong(EXTRA_BUDGET_ID, budget.id!!)
                }
                navController.navigate(R.id.categoryListFragment, bundle)
            }
        }
    }
}