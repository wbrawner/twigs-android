package com.wbrawner.budget.ui.categories

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.di.BudgetViewModelFactory
import com.wbrawner.budget.ui.EXTRA_CATEGORY_ID
import com.wbrawner.budget.ui.transactions.toLong
import kotlinx.android.synthetic.main.activity_add_edit_category.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CategoryFormActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Main
    lateinit var viewModel: CategoryViewModel
    var id: Long? = null
    var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_category)
        setSupportActionBar(action_bar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (application as AllowanceApplication).appComponent.inject(this)
        launch {
            val budgets = viewModel.getBudgets().toTypedArray()
            budgetSpinner.adapter = ArrayAdapter<Budget>(
                    this@CategoryFormActivity,
                    android.R.layout.simple_list_item_1,
                    budgets
            )
            loadCategory()
        }
    }

    private fun loadCategory() {
        val categoryId = intent?.extras?.getLong(EXTRA_CATEGORY_ID)
        if (categoryId == null) {
            setTitle(R.string.title_add_category)
            return
        }
        launch {
            val category = try {
                viewModel.getCategory(categoryId)
            } catch (e: Exception) {
                menu?.findItem(R.id.action_delete)?.isVisible = false
                null
            } ?: return@launch
            id = category.id
            setTitle(R.string.title_edit_category)
            menu?.findItem(R.id.action_delete)?.isVisible = true
            edit_category_name.setText(category.title)
            edit_category_amount.setText(String.format("%.02f", category.amount / 100.0f))
            expense.isChecked = category.expense
            income.isChecked = !category.expense
            archived.isChecked = category.archived
        }
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
                launch {
                    viewModel.saveCategory(Category(
                            id = id,
                            title = edit_category_name.text.toString(),
                            amount = edit_category_amount.text.toLong(),
                            budgetId = (budgetSpinner.selectedItem as Budget).id!!,
                            expense = expense.isChecked,
                            archived = archived.isChecked
                    ))
                    finish()
                }
            }
            R.id.action_delete -> {
                launch {
                    viewModel.deleteCategoryById(this@CategoryFormActivity.id!!)
                    finish()
                }
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
