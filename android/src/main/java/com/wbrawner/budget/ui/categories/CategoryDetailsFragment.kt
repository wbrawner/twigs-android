package com.wbrawner.budget.ui.categories


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.R
import com.wbrawner.budget.TwigsApplication
import com.wbrawner.budget.ui.EXTRA_BUDGET_ID
import com.wbrawner.budget.ui.EXTRA_CATEGORY_ID
import com.wbrawner.budget.ui.EXTRA_CATEGORY_NAME
import com.wbrawner.budget.ui.toAmountSpannable
import com.wbrawner.budget.ui.transactions.TransactionListFragment
import kotlinx.android.synthetic.main.fragment_category_details.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class CategoryDetailsFragment : Fragment() {
    val viewModel: CategoryDetailsViewModel by viewModels()

    override fun onAttach(context: Context) {
        (requireActivity().application as TwigsApplication).appComponent.inject(viewModel)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_category_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.title = arguments?.getString(EXTRA_CATEGORY_NAME)
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is AsyncState.Loading -> {
                        categoryDetails.visibility = View.GONE
                        progressBar.visibility = View.VISIBLE
                    }
                    is AsyncState.Success -> {
                        categoryDetails.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        val category = state.data.category
                        activity?.title = category.title
                        val tintColor =
                            if (category.expense) R.color.colorTextRed else R.color.colorTextGreen
                        val colorStateList = with(view.context) {
                            android.content.res.ColorStateList.valueOf(getColor(tintColor))
                        }
                        categoryProgress.progressTintList = colorStateList
                        categoryProgress.max = category.amount.toInt()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            categoryProgress.setProgress(
                                state.data.balance.toInt(),
                                true
                            )
                        } else {
                            categoryProgress.progress = state.data.balance.toInt()
                        }
                        total.text = category.amount.toAmountSpannable()
                        balance.text = state.data.balance.toAmountSpannable()
                        remaining.text = state.data.remaining.toAmountSpannable()
                        if (category.description.isNullOrBlank()) {
                            categoryDescription.visibility = View.GONE
                        } else {
                            categoryDescription.visibility = View.VISIBLE
                            categoryDescription.text = category.description
                        }
                        childFragmentManager.fragments.firstOrNull()?.let {
                            if (it !is TransactionListFragment) return@let
                            it.reloadItems()
                        } ?: run {
                            val transactionsFragment = TransactionListFragment().apply {
                                arguments = Bundle().apply {
                                    putString(EXTRA_BUDGET_ID, category.budgetId)
                                    putString(EXTRA_CATEGORY_ID, category.id)
                                }
                            }
                            childFragmentManager.beginTransaction()
                                .replace(R.id.transactionsFragmentContainer, transactionsFragment)
                                .commit()
                        }
                    }
                    is AsyncState.Error -> {
                        categoryDetails.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        Toast.makeText(view.context, "Failed to load context", Toast.LENGTH_SHORT)
                            .show()
                    }
                    is AsyncState.Exit -> {
                        findNavController().navigateUp()
                    }
                }
            }
        }
        viewModel.getCategory(arguments?.getString(EXTRA_CATEGORY_ID))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_edit) {
            val bundle = Bundle().apply {
                putString(EXTRA_CATEGORY_ID, arguments?.getString(EXTRA_CATEGORY_ID))
            }
            findNavController().navigate(R.id.addEditCategoryActivity, bundle)
        } else if (item.itemId == android.R.id.home) {
            return findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_editable, menu)
    }
}
