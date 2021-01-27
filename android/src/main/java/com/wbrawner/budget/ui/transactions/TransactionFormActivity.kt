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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder
import com.wbrawner.budget.AllowanceApplication
import com.wbrawner.budget.R
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.category.Category
import com.wbrawner.budget.common.transaction.Transaction
import com.wbrawner.budget.ui.EXTRA_TRANSACTION_ID
import com.wbrawner.budget.ui.MainActivity
import kotlinx.android.synthetic.main.activity_add_edit_transaction.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*
import kotlin.coroutines.CoroutineContext

class TransactionFormActivity : AppCompatActivity(), CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Main

    private val viewModel: TransactionFormViewModel by viewModels()
    var id: String? = null
    var menu: Menu? = null
    var transaction: Transaction? = null

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_transaction)
        setSupportActionBar(action_bar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.title_add_transaction)
        edit_transaction_type_expense.isChecked = true
        (application as AllowanceApplication).appComponent.inject(viewModel)
        viewModel.init()
        launch {
            val accounts = viewModel.getAccounts().toTypedArray()
            setCategories()
            budgetSpinner.adapter = ArrayAdapter<Budget>(
                    this@TransactionFormActivity,
                    android.R.layout.simple_list_item_1,
                    accounts
            )
            container_edit_transaction_type.setOnCheckedChangeListener { _, _ ->
                this@TransactionFormActivity.launch {
                    val budget = budgetSpinner.selectedItem as Budget
                    setCategories(viewModel.getCategories(budget.id!!, edit_transaction_type_expense.isChecked))
                }
            }
            budgetSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    this@TransactionFormActivity.launch {
                        val budget = budgetSpinner.selectedItem as Budget
                        setCategories(viewModel.getCategories(budget.id!!, edit_transaction_type_expense.isChecked))
                    }
                }
            }
            loadTransaction()
            transactionDate.setOnClickListener {
                val currentDate = DateFormat.getDateFormat(this@TransactionFormActivity)
                        .parse(transactionDate.text.toString()) ?: Date()
                DatePickerDialog(
                        this@TransactionFormActivity,
                        { _, year, month, dayOfMonth ->
                            transactionDate.text = DateFormat.getDateFormat(this@TransactionFormActivity)
                                    .format(Date(year, month, dayOfMonth))
                        },
                        currentDate.year + 1900,
                        currentDate.month,
                        currentDate.date
                )
                        .show()
            }
            transactionTime.setOnClickListener {
                val currentDate = DateFormat.getTimeFormat(this@TransactionFormActivity)
                        .parse(transactionTime.text.toString()) ?: Date()
                TimePickerDialog(
                        this@TransactionFormActivity,
                        { _, hourOfDay, minute ->
                            val newTime = Date().apply {
                                hours = hourOfDay
                                minutes = minute
                            }
                            transactionTime.text = DateFormat.getTimeFormat(this@TransactionFormActivity)
                                    .format(newTime)
                        },
                        currentDate.hours,
                        currentDate.minutes,
                        DateFormat.is24HourFormat(this@TransactionFormActivity)
                ).show()
            }
        }
    }

    private suspend fun loadTransaction() {
        transaction = try {
            viewModel.getTransaction(intent!!.extras!!.getString(EXTRA_TRANSACTION_ID)!!)
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

    private fun setCategories(categories: List<Category> = emptyList()) {
        val adapter = ArrayAdapter<Category>(
                this@TransactionFormActivity,
                android.R.layout.simple_list_item_1
        )
        adapter.add(Category(title = getString(R.string.uncategorized),
                amount = 0, budgetId = ""))
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onNavigateUp()
            R.id.action_save -> {
                val date = GregorianCalendar.getInstance().apply {
                    DateFormat.getDateFormat(this@TransactionFormActivity)
                            .parse(transactionDate.text.toString())
                            ?.let {
                                time = it
                            }
                    DateFormat.getTimeFormat(this@TransactionFormActivity)
                            .parse(transactionTime.text.toString())
                            ?.let { GregorianCalendar.getInstance().apply { time = it } }
                            ?.let {
                                set(Calendar.HOUR_OF_DAY, it.get(Calendar.HOUR_OF_DAY))
                                set(Calendar.MINUTE, it.get(Calendar.MINUTE))
                            }
                }
                val categoryId = (edit_transaction_category.selectedItem as? Category)?.id
                launch {
                    viewModel.saveTransaction(Transaction(
                            id = id,
                            budgetId = (budgetSpinner.selectedItem as Budget).id!!,
                            title = edit_transaction_title.text.toString(),
                            date = date.time,
                            description = edit_transaction_description.text.toString(),
                            amount = (BigDecimal(edit_transaction_amount.text.toString()) * 100.toBigDecimal()).toLong(),
                            expense = edit_transaction_type_expense.isChecked,
                            categoryId = categoryId,
                            createdBy = viewModel.currentUserId!!
                    ))
                    onNavigateUp()
                }
            }
            R.id.action_delete -> {
                launch {
                    viewModel.deleteTransaction(this@TransactionFormActivity.id!!)
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
