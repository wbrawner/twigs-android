package com.wbrawner.budget.ui.budgets


import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.wbrawner.budget.R
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.user.User
import com.wbrawner.budget.ui.EXTRA_BUDGET_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_edit_budget.*

@AndroidEntryPoint
class AddEditBudgetFragment : Fragment() {
    private val viewModel: BudgetFormViewModel by viewModels()

    var id: String? = null
    var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel.getBudget(arguments?.getString(EXTRA_BUDGET_ID))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_edit_budget, container, false)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add_edit, menu)
        if (id != null) {
            menu.findItem(R.id.action_delete)?.isVisible = true
        }
        this.menu = menu
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val suggestionsAdapter = ArrayAdapter<User>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf()
        )
        usersSearch.setAdapter(suggestionsAdapter)
        viewModel.userSuggestions.observe(viewLifecycleOwner, Observer {
            suggestionsAdapter.clear()
            suggestionsAdapter.addAll(it)
        })
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is BudgetFormState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    budgetForm.visibility = View.GONE
                }
                is BudgetFormState.Success -> {
                    budgetForm.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    activity?.setTitle(state.titleRes)
                    menu?.findItem(R.id.action_delete)?.isVisible = state.showDeleteButton
                    id = state.budget.id
                    name.setText(state.budget.name)
                    description.setText(state.budget.description)
                }
                is BudgetFormState.Exit -> {
                    findNavController().navigateUp()
                }
                else -> { /* no-op */ }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.action_save -> {
                viewModel.saveBudget(Budget(
                    id = id,
                    name = name.text.toString(),
                    description = description.text.toString(),
                    users = emptyList()
                ))
            }
            R.id.action_delete -> {
                viewModel.deleteBudget(this@AddEditBudgetFragment.id!!)
            }
        }
        return true
    }
}
