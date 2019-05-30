package com.wbrawner.budget.ui.transactions

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.ViewModelProviders
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.common.account.Account
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.di.BudgetViewModelFactory
import com.wbrawner.budget.ui.MainActivity
import com.wbrawner.budget.ui.autoDispose
import com.wbrawner.budget.ui.fromBackgroundToMain
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_add_edit_transaction.*
import java.util.*
import javax.inject.Inject

const val EXTRA_ACCOUNT_ID = "EXTRA_ACCOUNT_ID"
const val EXTRA_TRANSACTION_ID = "EXTRA_TRANSACTION_ID"

class AddEditTransactionActivity : AppCompatActivity() {
    private val disposables = CompositeDisposable()
    private lateinit var account: Account
    @Inject
    lateinit var viewModelFactory: BudgetViewModelFactory
    private lateinit var viewModel: AddEditTransactionViewModel
    var id: Long? = null
    var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent?.hasExtra(EXTRA_ACCOUNT_ID) != true) {
            finish()
            return
        }
        setContentView(R.layout.activity_add_edit_transaction)
        setSupportActionBar(action_bar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.title_add_transaction)
        edit_transaction_type_expense.isChecked = true
        (application as AllowanceApplication).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddEditTransactionViewModel::class.java)
        viewModel.getAccount(intent!!.extras!!.getLong(EXTRA_ACCOUNT_ID))
                .fromBackgroundToMain()
                .subscribe { account, err ->
                    if (err != null) {
                        finish()
                        // TODO: Hide a progress spinner and show error message
                        return@subscribe
                    }
                    this@AddEditTransactionActivity.account = account
                    loadCategories()
                }
                .autoDispose(disposables)
    }

    private fun loadCategories() {
        viewModel.getCategories(account.id!!)
                .fromBackgroundToMain()
                .subscribe { categories ->
                    val adapter = ArrayAdapter<Category>(
                            this@AddEditTransactionActivity,
                            android.R.layout.simple_list_item_1
                    )
                    adapter.add(Category(id = 0, title = getString(R.string.uncategorized),
                            amount = 0, accountId = 0))
                    // TODO: Add option to create new category on-the-fly
                    if (!categories.isEmpty()) {
                        adapter.addAll(categories)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        edit_transaction_category.adapter = adapter
                    }
                    if (intent?.hasExtra(EXTRA_TRANSACTION_ID) == true) {
                        loadTransaction()
                    }
                }
                .autoDispose(disposables)

    }

    private fun loadTransaction() {
        viewModel.getTransaction(intent!!.extras!!.getLong(EXTRA_TRANSACTION_ID))
                .fromBackgroundToMain()
                .subscribe { transaction, err ->
                    if (err != null) {
                        menu?.findItem(R.id.action_delete)?.isVisible = false
                        return@subscribe
                    }
                    id = transaction.id
                    menu?.findItem(R.id.action_delete)?.isVisible = true
                    edit_transaction_title.setText(transaction.title)
                    edit_transaction_description.setText(transaction.description)
                    edit_transaction_amount.setText(String.format("%.02f", transaction.amount / 100.0f))
                    if (transaction.expense) {
                        edit_transaction_type_expense.isChecked = true
                    } else {
                        edit_transaction_type_income.isChecked = true
                    }
                    val field = Calendar.getInstance()
                    field.time = transaction.date
                    val year = field.get(Calendar.YEAR)
                    val month = field.get(Calendar.MONTH)
                    val day = field.get(Calendar.DAY_OF_MONTH)
                    edit_transaction_date.updateDate(year, month, day)
                    transaction.categoryId?.let {
                        for (i in 0 until edit_transaction_category.adapter.count) {
                            if (it == (edit_transaction_category.adapter.getItem(i) as Category).id) {
                                edit_transaction_category.setSelection(i)
                                break
                            }
                        }
                    }
                }
                .autoDispose(disposables)
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
            android.R.id.home -> onNavigateUp()
            R.id.action_save -> {
                val field = edit_transaction_date
                val cal = Calendar.getInstance()
                cal.set(field.year, field.month, field.dayOfMonth)
                var categoryId = (edit_transaction_category.selectedItem as? Category)?.id
                        ?.let {
                            if (it > 0) it
                            else null
                        }
                viewModel.saveTransaction(Transaction(
                        id = id,
                        accountId = account.id!!,
                        title = edit_transaction_title.text.toString(),
                        date = cal.time,
                        description = edit_transaction_description.text.toString(),
                        amount = edit_transaction_amount.rawValue,
                        expense = edit_transaction_type_expense.isChecked,
                        categoryId = categoryId,
                        createdBy = (application as AllowanceApplication).currentUser!!.id!!
                ))
                        .fromBackgroundToMain()
                        .subscribe { transaction, err ->
                            onNavigateUp()
                        }
                        .autoDispose(disposables)
            }
            R.id.action_delete -> {
                viewModel.deleteTransaction(this@AddEditTransactionActivity.id!!)
                        .fromBackgroundToMain()
                        .subscribe { _, err ->
                            err?.printStackTrace()
                            onNavigateUp()
                        }
            }
        }
        return true
    }

    override fun onNavigateUp(): Boolean {
        val upIntent: Intent = NavUtils.getParentActivityIntent(this)
                ?: throw IllegalStateException("No Parent Activity Intent")

        upIntent.putExtra(MainActivity.EXTRA_OPEN_FRAGMENT, TransactionListFragment.TAG_FRAGMENT)
        when {
            NavUtils.shouldUpRecreateTask(this, upIntent) || isTaskRoot -> {
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(upIntent)
                        .startActivities()
            }
            else -> {
                finish()
            }
        }

        return true
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }
}
