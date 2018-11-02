package com.wbrawner.budget.ui.transactions

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.wbrawner.budget.R
import com.wbrawner.budget.data.model.Transaction
import com.wbrawner.budget.data.model.TransactionCategory
import com.wbrawner.budget.data.model.TransactionWithCategory
import kotlinx.android.synthetic.main.activity_add_edit_transaction.*
import java.util.*

class AddEditTransactionActivity : AppCompatActivity() {
    lateinit var viewModel: TransactionViewModel
    var id: Int? = null
    var date: Date = Date()
    var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_transaction)
        setSupportActionBar(action_bar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.title_add_transaction)
        edit_transaction_type_expense.isChecked = true
        viewModel = ViewModelProviders.of(this).get(TransactionViewModel::class.java)
        viewModel.getCategories()
                .observe(this, Observer<List<TransactionCategory>> { categories ->
                    val adapter = ArrayAdapter<TransactionCategory>(
                            this@AddEditTransactionActivity,
                            android.R.layout.simple_list_item_1
                    )

                    adapter.add(TransactionCategory(0, getString(R.string.uncategorized)))
                    if (categories == null || categories.isEmpty()) {
                        return@Observer
                    }

                    adapter.addAll(categories)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    edit_transaction_category.adapter = adapter
                })

        if (intent?.hasExtra(EXTRA_TRANSACTION_ID) != true) {
            return
        }

        viewModel.getTransaction(intent!!.extras!!.getInt(EXTRA_TRANSACTION_ID))
                .observe(this, Observer<TransactionWithCategory> { transactionWithCategory ->
                    if (transactionWithCategory == null) {
                        menu?.findItem(R.id.action_delete)?.isVisible = false
                        return@Observer
                    }
                    val transaction = transactionWithCategory.transaction
                    id = transaction.id
                    menu?.findItem(R.id.action_delete)?.isVisible = true
                    edit_transaction_title.setText(transaction.name)
                    edit_transaction_description.setText(transaction.description)
                    edit_transaction_amount.setText(String.format("%.02f", transaction.amount / 100.0f))
                    if (transaction.isExpense) {
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
                    if (transactionWithCategory.categorySet.isNotEmpty()) {
                        val category = transactionWithCategory.categorySet.first()
                        for (i in 0 until edit_transaction_category.adapter.count) {
                            if (category.id == (edit_transaction_category.adapter.getItem(i) as TransactionCategory).id) {
                                edit_transaction_category.setSelection(i)
                                break
                            }
                        }
                    }
                })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_edit, menu)
        if (id != null) {
            menu?.findItem(R.id.action_delete)?.isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_save -> {
                val field = edit_transaction_date
                val cal = Calendar.getInstance()
                cal.set(field.year, field.month, field.dayOfMonth)

                viewModel.saveTransaction(Transaction(
                        id = id,
                        name = edit_transaction_title.text.toString(),
                        date = cal.time,
                        description = edit_transaction_description.text.toString(),
                        amount = edit_transaction_amount.rawValue.toInt(),
                        isExpense = edit_transaction_type_expense.isChecked,
                        categoryId = (edit_transaction_category.selectedItem as TransactionCategory).id,
                        remoteId = null
                ))
                finish()
            }
            R.id.action_delete -> {
                viewModel.deleteTransactionById(this@AddEditTransactionActivity.id!!)
                finish()
            }
        }
        return true
    }


    companion object {
        const val EXTRA_TRANSACTION_ID = "EXTRA_TRANSACTION_ID"
    }
}
