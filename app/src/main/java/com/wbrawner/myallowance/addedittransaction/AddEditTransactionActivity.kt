package com.wbrawner.myallowance.addedittransaction

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.wbrawner.myallowance.R
import com.wbrawner.myallowance.data.Transaction
import com.wbrawner.myallowance.data.TransactionType
import com.wbrawner.myallowance.transactions.TransactionViewModel
import kotlinx.android.synthetic.main.activity_add_edit_transaction.*
import java.util.*

class AddEditTransactionActivity : AppCompatActivity() {
    lateinit var viewModel: TransactionViewModel
    lateinit var type: TransactionType
    var date: Date = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_transaction)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel = ViewModelProviders.of(this).get(TransactionViewModel::class.java)
        type = TransactionType.valueOf(intent?.extras?.getString(EXTRA_TYPE, "EXPENSE")?: "EXPENSE")
        setTitle(type.addTitle)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_edit, menu)
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
                        id = null,
                        title = edit_transaction_title.text.toString(),
                        date = cal.time,
                        description = edit_transaction_description.text.toString(),
                        amount = edit_transaction_amount.text.toString().toDouble(),
                        type = type
                ))
                finish()
            }
        }
        return true
    }


    companion object {
        const val EXTRA_TYPE = "EXTRA_TRANSACTION_TYPE"
        const val EXTRA_TRANSACTION_ID = "EXTRA_TRANSACTION_ID"
    }
}
