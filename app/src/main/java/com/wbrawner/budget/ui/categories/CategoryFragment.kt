package com.wbrawner.budget.ui.categories


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.ui.EXTRA_CATEGORY_ID
import com.wbrawner.budget.ui.base.BindableAdapter
import com.wbrawner.budget.ui.base.BindableState
import com.wbrawner.budget.ui.base.ListWithAddButtonFragment
import com.wbrawner.budget.ui.transactions.TRANSACTION_VIEW
import com.wbrawner.budget.ui.transactions.TransactionState
import com.wbrawner.budget.ui.transactions.TransactionViewHolder

/**
 * A simple [Fragment] subclass.
 */
class CategoryFragment : ListWithAddButtonFragment<CategoryViewModel>() {
    override val noItemsStringRes: Int = R.string.transactions_no_data
    override val viewModelClass: Class<CategoryViewModel> = CategoryViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as AllowanceApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override suspend fun loadItems(): Pair<List<BindableState>, Map<Int, (view: View) -> BindableAdapter.BindableViewHolder<in BindableState>>> {
        val categoryId = arguments?.getLong(EXTRA_CATEGORY_ID)
        if (categoryId == null) {
            findNavController().navigateUp()
            return Pair(emptyList(), emptyMap())
        }
        val category = viewModel.getCategory(categoryId)
        activity?.title = category.title
        // TODO: Add category details here as well
        val items = ArrayList<BindableState>()
        items.addAll(viewModel.getTransactions(categoryId).map { TransactionState(it) })
        return Pair(
                items,
                mapOf(TRANSACTION_VIEW to { v ->
                    TransactionViewHolder(v, findNavController()) as BindableAdapter.BindableViewHolder<in BindableState>
                })
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_edit) {
            val bundle = Bundle().apply {
                putLong(EXTRA_CATEGORY_ID, arguments?.getLong(EXTRA_CATEGORY_ID) ?: -1)
            }
            findNavController().navigate(R.id.addEditCategoryActivity, bundle)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_editable, menu)
    }

    override fun addItem() {
        // TODO: Open new transaction flow with budget and category pre-filled
        findNavController().navigate(R.id.addEditTransactionActivity)
    }
}
