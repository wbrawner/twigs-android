package com.wbrawner.budget.ui.categories

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.Observer
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.R
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.ui.EXTRA_CATEGORY_ID
import com.wbrawner.budget.ui.transactions.toLong
import kotlinx.android.synthetic.main.activity_add_edit_category.*

class CategoryFormActivity : AppCompatActivity() {
    lateinit var viewModel: CategoryFormViewModel
    var id: Long? = null
    var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_category)
        setSupportActionBar(action_bar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (application as AllowanceApplication).appComponent.inject(viewModel)
        viewModel.state.observe(this, Observer { state ->
            when (state) {
                is AsyncState.Loading -> {
                    categoryForm.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                }
                is AsyncState.Success -> {
                    categoryForm.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    val category = state.data.category
                    id = category.id
                    setTitle(state.data.titleRes)
                    menu?.findItem(R.id.action_delete)?.isVisible = state.data.showDeleteButton
                    edit_category_name.setText(category.title)
                    edit_category_amount.setText(String.format("%.02f", (category.amount.toBigDecimal() / 100.toBigDecimal()).toFloat()))
                    expense.isChecked = category.expense
                    income.isChecked = !category.expense
                    archived.isChecked = category.archived
                    budgetSpinner.adapter = ArrayAdapter<Budget>(
                            this@CategoryFormActivity,
                            android.R.layout.simple_list_item_1,
                            state.data.budgets
                    )
                }
                is AsyncState.Error -> {
                    // TODO: Show error message
                    categoryForm.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Failed to save Category", Toast.LENGTH_SHORT).show()
                }
                is AsyncState.Exit -> finish()
            }
        })
        viewModel.loadCategory(intent?.extras?.getLong(EXTRA_CATEGORY_ID))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_edit, menu)
        if (id != null) {
            menu?.findItem(R.id.action_delete)?.isVisible = true
        }
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                val upIntent: Intent? = NavUtils.getParentActivityIntent(this)

                when {
                    upIntent == null -> throw IllegalStateException("No Parent Activity Intent")
                    NavUtils.shouldUpRecreateTask(this, upIntent) || isTaskRoot -> {
                        TaskStackBuilder.create(this)
                                .addNextIntentWithParentStack(upIntent)
                                .startActivities()
                    }
                    else -> {
                        NavUtils.navigateUpTo(this, upIntent)
                    }
                }
            }
            R.id.action_save -> {
                if (!validateFields()) return true
                viewModel.saveCategory(Category(
                        id = id,
                        title = edit_category_name.text.toString(),
                        amount = edit_category_amount.text.toLong(),
                        budgetId = (budgetSpinner.selectedItem as Budget).id!!,
                        expense = expense.isChecked,
                        archived = archived.isChecked
                ))
            }
            R.id.action_delete -> {
                viewModel.deleteCategoryById(this@CategoryFormActivity.id!!)
            }
        }
        return true
    }

    private fun validateFields(): Boolean {
        var errors = false
        if (edit_category_name.text?.isEmpty() == true) {
            edit_category_name.error = getString(R.string.required_field_name)
            errors = true
        }

        if (edit_category_amount.text.toString().isEmpty()) {
            edit_category_amount.error = getString(R.string.required_field_amount)
            errors = true
        }

        return !errors
    }
}
