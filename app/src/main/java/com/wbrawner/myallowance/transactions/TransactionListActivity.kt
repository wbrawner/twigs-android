package com.wbrawner.myallowance.transactions

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.StringRes
import com.wbrawner.myallowance.R
import com.wbrawner.myallowance.data.TransactionType
import com.wbrawner.myallowance.overview.OverviewFragment
import kotlinx.android.synthetic.main.activity_transaction_list.*

class TransactionListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)

        menu_main.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_expenses -> updateFragment(TransactionType.EXPENSE)
                R.id.action_income -> updateFragment(TransactionType.INCOME)
                else ->
                    updateFragment(OverviewFragment.TAG_FRAGMENT, OverviewFragment.TITLE_FRAGMENT)
            }
            true
        }
        menu_main.selectedItemId = R.id.action_overview
    }

    private fun updateFragment(type: TransactionType) {
        updateFragment(type.name, type.title)
    }

    private fun updateFragment(tag: String, @StringRes title: Int) {
        setTitle(title)
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        val ft = supportFragmentManager.beginTransaction()
        if (fragment == null) {
            fragment = if (tag == "overview") OverviewFragment()
            else
                TransactionListFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(TransactionListFragment.ARG_TYPE, TransactionType.valueOf(tag))
                    }
                }
            ft.add(R.id.content_container, fragment, tag)
        }
        for (fmFragment in supportFragmentManager.fragments) {
            if (fmFragment == fragment) {
                ft.show(fmFragment)
            } else {
                ft.hide(fmFragment)
            }
        }

        ft.commit()
    }
}
