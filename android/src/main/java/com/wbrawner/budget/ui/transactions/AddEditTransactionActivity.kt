package com.wbrawner.budget.ui.transactions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.ViewModelProviders
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.di.BudgetViewModelFactory
import com.wbrawner.budget.ui.EXTRA_TRANSACTION_ID
import com.wbrawner.budget.ui.MainActivity
import kotlinx.android.synthetic.main.activity_add_edit_transaction.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AddEditTransactionActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Main
    @Inject
    lateinit var viewModelFactory: BudgetViewModelFactory
    private lateinit var viewModel: AddEditTransactionViewModel
    var id: Long? = null
    var menu: Menu? = null
    var transaction: Transaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_transaction)
        setSupportActionBar(action_bar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.title_add_transaction)
        edit_transaction_type_expense.isChecked = true
        (application as AllowanceApplication).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddEditTransactionViewModel::class.java)
        launch {
            val accounts = viewModel.getAccounts().toTypedArray()
            setCategories()
            budgetSpinner.adapter = ArrayAdapter<Budget>(
                    this@AddEditTransactionActivity,
                    android.R.layout.simple_list_item_1,
                    accounts
            )

            budgetSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    this@AddEditTransactionActivity.launch {
                        val account = budgetSpinner.selectedItem as Budget
                        setCategories(viewModel.getCategories(account.id!!))
                    }
                }
            }
            loadTransaction()
            transactionDate.setOnClickListener {
                val currentDate = DateFormat.getDateFormat(this@AddEditTransactionActivity)
                        .parse(transactionDate.text.toString())
                DatePickerDialog(
                        this@AddEditTransactionActivity,
                        { _, year, month, dayOfMonth ->
                            transactionDate.text = DateFormat.getDateFormat(this@AddEditTransactionActivity)
                                    .format(Date(year, month, dayOfMonth))
                        },
                        currentDate.year + 1900,
                        currentDate.month,
                        currentDate.date
                ).show()
            }
            transactionTime.setOnClickListener {
                val currentDate = DateFormat.getTimeFormat(this@AddEditTransactionActivity)
                        .parse(transactionTime.text.toString())
                TimePickerDialog(
                        this@AddEditTransactionActivity,
                        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                            val newTime = Date().apply {
                                hours = hourOfDay
                                minutes = minute
                            }
                            transactionTime.text = DateFormat.getTimeFormat(this@AddEditTransactionActivity)
                                    .format(newTime)
                        },
                        currentDate.hours,
                        currentDate.minutes,
                        DateFormat.is24HourFormat(this@AddEditTransactionActivity)
                ).show()
            }
        }
    }

    private suspend fun loadTransaction() {
        transaction = try {
            viewModel.getTransaction(intent!!.extras!!.getLong(EXTRA_TRANSACTION_ID))
        } catch (e: Exception) {
            menu?.findItem(R.id.action_delete)?.isVisible = false
            val date = Date()
            transactionDate.text = DateFormat.getDateFormat(this).format(date)
            transactionTime.text = DateFormat.getTimeFormat(this).format(date)
            return
        }
        setTitle(R.string.title_edit_transaction)
        id = transaction?.id
        menu?.findItem(R.id.action_delete)?.isVisible = true
        edit_transaction_title.setText(transaction?.title)
        edit_transaction_description.setText(transaction?.description)
        edit_transaction_amount.setText(String.format("%.02f", transaction!!.amount / 100.0f))
        if (transaction!!.expense) {
            edit_transaction_type_expense.isChecked = true
        } else {
            edit_transaction_type_income.isChecked = true
        }
        transactionDate.text = DateFormat.getDateFormat(this).format(transaction!!.date)
        transactionTime.text = DateFormat.getTimeFormat(this).format(transaction!!.date)
        transaction?.categoryId?.let {
            for (i in 0 until edit_transaction_category.adapter.count) {
                if (it == (edit_transaction_category.adapter.getItem(i) as Category).id) {
                    edit_transaction_category.setSelection(i)
                    break
                }
            }
        }
    }

    private fun setCategories(categories: Collection<Category> = emptyList()) {
        val adapter = ArrayAdapter<Category>(
                this@AddEditTransactionActivity,
                android.R.layout.simple_list_item_1
        )
        adapter.add(Category(id = 0, title = getString(R.string.uncategorized),
                amount = 0, budgetId = 0))
        adapter.addAll(categories)
        edit_transaction_category.adapter = adapter
        transaction?.categoryId?.let {
            for (i in 0 until edit_transaction_category.adapter.count) {
                if (it == (edit_transaction_category.adapter.getItem(i) as Category).id) {
                    edit_transaction_category.setSelection(i)
                    break
                }
            }
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
            android.R.id.home -> onNavigateUp()
            R.id.action_save -> {
                val date = DateFormat.getDateFormat(this).parse(transactionDate.text.toString())
                val time = DateFormat.getTimeFormat(this).parse(transactionTime.text.toString())
                date.hours = time.hours
                date.minutes = time.minutes
                val categoryId = (edit_transaction_category.selectedItem as? Category)?.id
                        ?.let {
                            if (it > 0) it
                            else null
                        }
                launch {
                    viewModel.saveTransaction(Transaction(
                            id = id,
                            budgetId = (budgetSpinner.selectedItem as Budget).id!!,
                            title = edit_transaction_title.text.toString(),
                            date = date,
                            description = edit_transaction_description.text.toString(),
                            amount = edit_transaction_amount.text.toLong(),
                            expense = edit_transaction_type_expense.isChecked,
                            categoryId = categoryId,
                            createdBy = (application as AllowanceApplication).currentUser!!.id!!
                    ))
                    onNavigateUp()
                }
            }
            R.id.action_delete -> {
                launch {
                    viewModel.deleteTransaction(this@AddEditTransactionActivity.id!!)
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
}

fun Editable?.toLong(): Long = toString().toDouble().toLong() * 100
