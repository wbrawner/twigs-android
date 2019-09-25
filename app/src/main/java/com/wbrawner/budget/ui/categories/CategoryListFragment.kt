package com.wbrawner.budget.ui.categories

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.ui.EXTRA_BUDGET_ID
import com.wbrawner.budget.ui.EXTRA_CATEGORY_ID
import com.wbrawner.budget.ui.base.BindableAdapter
import com.wbrawner.budget.ui.base.BindableState
import com.wbrawner.budget.ui.base.ListWithAddButtonFragment
import kotlinx.coroutines.launch

class CategoryListFragment : ListWithAddButtonFragment<CategoryViewModel>() {
    override val noItemsStringRes: Int = R.string.categories_no_data
    override val viewModelClass: Class<CategoryViewModel> = CategoryViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as AllowanceApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override suspend fun loadItems(): Pair<List<BindableState>, Map<Int, (view: View) -> BindableAdapter.BindableViewHolder<in BindableState>>> {
        val budgetId = arguments?.getLong(EXTRA_BUDGET_ID)
        if (budgetId == null) {
            findNavController().navigateUp()
            return Pair(emptyList(), emptyMap())
        }
        val budget = viewModel.getBudget(budgetId)
        activity?.title = budget.name
        return Pair(
                viewModel.getCategories(budgetId).map { CategoryState(it) },
                mapOf(CATEGORY_VIEW to { v ->
                    CategoryViewHolder(v, viewModel, findNavController()) as BindableAdapter.BindableViewHolder<in BindableState>
                })
        )
    }

    override fun addItem() {
        startActivity(Intent(activity, AddEditCategoryActivity::class.java))
    }

    companion object {
        const val TAG_FRAGMENT = "categories"
    }
}

const val CATEGORY_VIEW = R.layout.list_item_category

class CategoryState(val category: Category) : BindableState {
    override val viewType: Int = CATEGORY_VIEW
}

class CategoryViewHolder(
        itemView: View,
        private val viewModel: CategoryViewModel,
        private val navController: NavController
) : BindableAdapter.CoroutineViewHolder<CategoryState>(itemView) {
    private val name: TextView = itemView.findViewById(R.id.category_title)
    private val amount: TextView = itemView.findViewById(R.id.category_amount)
    private val progressBar: ProgressBar = itemView.findViewById(R.id.category_progress)

    @SuppressLint("NewApi")
    override fun onBind(item: CategoryState) {
        with(item) {
            name.text = category.title
            // TODO: Format according to budget's currency
            amount.text = String.format("${'$'}%.02f", category.amount / 100.0f)
            progressBar.max = category.amount.toInt()
            launch {
                val balance = viewModel.getBalance(category.id!!)
                progressBar.isIndeterminate = false
                progressBar.setProgress(
                        (-1 * balance).toInt(),
                        true
                )
                amount.text = itemView.context.getString(
                        R.string.balance_remaning,
                        (category.amount + balance) / 100.0f
                )
            }
            itemView.setOnClickListener {
                val bundle = Bundle().apply {
                    putLong(EXTRA_CATEGORY_ID, category.id ?: -1)
                }
                navController.navigate(R.id.categoryFragment, bundle)
            }
        }
    }
}