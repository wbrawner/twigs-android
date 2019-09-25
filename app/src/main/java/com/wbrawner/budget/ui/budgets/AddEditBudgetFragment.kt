package com.wbrawner.budget.ui.budgets


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.di.BudgetViewModelFactory
import com.wbrawner.budget.ui.EXTRA_BUDGET_ID
import kotlinx.android.synthetic.main.fragment_add_edit_budget.*
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class AddEditBudgetFragment : Fragment() {
    lateinit var viewModel: AddEditBudgetViewModel
    @Inject
    lateinit var viewModelFactory: BudgetViewModelFactory
    var id: Long? = null
    var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as AllowanceApplication)
                .appComponent
                .inject(this)
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)
                .get(AddEditBudgetViewModel::class.java)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit_budget, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add_edit, menu)
        if (id != null) {
            menu.findItem(R.id.action_delete)?.isVisible = true
        }
        this.menu = menu
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        usersSearch.setAdapter(viewModel.suggestionsAdapter)
        launch {
            val budget = try {
                viewModel.getBudget(arguments!!.getLong(EXTRA_BUDGET_ID))
            } catch (e: Exception) {
                return@launch
            }
            activity?.setTitle(R.string.title_edit_budget)
            menu?.findItem(R.id.action_delete)?.isVisible = true
            id = budget.id
            name.setText(budget.name)
            description.setText(budget.description)
            budget.users.forEach {
                viewModel.addUser(it)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
            R.id.action_save -> {
                launch {
                    viewModel.saveBudget(Budget(
                            id = id,
                            name = name.text.toString(),
                            description = description.text.toString(),
                            users = viewModel.users.value ?: emptyList()
                    ))
                    findNavController().navigateUp()
                }
            }
            R.id.action_delete -> {
                launch {
                    viewModel.deleteBudget(this@AddEditBudgetFragment.id!!)
                    findNavController().navigateUp()
                }
            }
        }
        return true
    }
}

fun AddEditBudgetFragment.launch(block: suspend CoroutineScope.() -> Unit) = viewModel.launch(block)