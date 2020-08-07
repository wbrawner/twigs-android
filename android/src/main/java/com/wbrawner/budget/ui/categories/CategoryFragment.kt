package com.wbrawner.budget.ui.categories


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.ui.EXTRA_CATEGORY_ID
import com.wbrawner.budget.ui.base.BindableAdapter
import com.wbrawner.budget.ui.base.ListWithAddButtonFragment
import com.wbrawner.budget.ui.transactions.TRANSACTION_VIEW
import com.wbrawner.budget.ui.transactions.TransactionData
import com.wbrawner.budget.ui.transactions.TransactionViewHolder

/**
 * A simple [Fragment] subclass.
 */
class CategoryFragment : ListWithAddButtonFragment<Category, CategoryViewModel>() {
    override val viewModel: CategoryViewModel by viewModels()
    override val noItemsStringRes: Int = R.string.transactions_no_data

    override fun reloadItems() {

    }

    override val constructors: Map<Int, (View) -> BindableAdapter.BindableViewHolder<Category>>
        get() = TODO("Not yet implemented")
    override val diffUtilItemCallback: DiffUtil.ItemCallback<Category>
        get() = TODO("Not yet implemented")

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as AllowanceApplication).appComponent.inject(viewModel)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override suspend fun loadItems(): Pair<List<TransactionData>, Map<Int, (view: View) -> BindableAdapter.BindableViewHolder<TransactionData>>> {
        val categoryId = arguments?.getLong(EXTRA_CATEGORY_ID)
        if (categoryId == null) {
            findNavController().navigateUp()
            return Pair(emptyList(), emptyMap())
        }
        val category = viewModel.getCategory(categoryId)
        activity?.title = category.title
        // TODO: Add category details here as well
        val items = ArrayList<TransactionData>()
        items.addAll(viewModel.getTransactions(categoryId).map { TransactionData(it) })
        return Pair(
                items,
                mapOf(TRANSACTION_VIEW to { v ->
                    TransactionViewHolder(v, findNavController())
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
