package com.wbrawner.budget.ui

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.emoji.text.EmojiCompat
import com.wbrawner.budget.R
import com.wbrawner.budget.common.account.Account
import com.wbrawner.budget.ui.categories.CategoryListFragment
import com.wbrawner.budget.ui.overview.OverviewFragment
import com.wbrawner.budget.ui.transactions.TransactionListFragment
import kotlinx.android.synthetic.main.activity_transaction_list.*

class MainActivity : AppCompatActivity() {
    private val account = Account(
            id = 2,
            name = "Wells Fargo Checking",
            currencyCode = "USD"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiCompat.init(androidx.emoji.bundled.BundledEmojiCompatConfig(this))
        setContentView(R.layout.activity_transaction_list)
        setSupportActionBar(action_bar)

        menu_main.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_transactions -> updateFragment(
                        TransactionListFragment.TAG_FRAGMENT,
                        TransactionListFragment.TITLE_FRAGMENT
                )
                R.id.action_categories -> updateFragment(
                        CategoryListFragment.TAG_FRAGMENT,
                        CategoryListFragment.TITLE_FRAGMENT
                )
                else ->
                    updateFragment(OverviewFragment.TAG_FRAGMENT, OverviewFragment.TITLE_FRAGMENT)
            }
            true
        }

        val openFragmentId = when (intent?.getStringExtra(EXTRA_OPEN_FRAGMENT)) {
            TransactionListFragment.TAG_FRAGMENT -> R.id.action_transactions
            CategoryListFragment.TAG_FRAGMENT -> R.id.action_categories
            else -> R.id.action_overview
        }

        menu_main.selectedItemId = openFragmentId
    }

    private fun updateFragment(tag: String, @StringRes title: Int) {
        setTitle(title)
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        val ft = supportFragmentManager.beginTransaction()
        if (fragment == null) {
            fragment = when (tag) {
                OverviewFragment.TAG_FRAGMENT -> OverviewFragment().apply { account = this@MainActivity.account }
                CategoryListFragment.TAG_FRAGMENT -> CategoryListFragment().apply { account = this@MainActivity.account }
                else -> {
                    TransactionListFragment().apply { account = this@MainActivity.account }
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

    companion object {
        const val EXTRA_OPEN_FRAGMENT = "com.wbrawner.budget.MainActivity.EXTRA_OPEN_FRAGMENT"
    }
}
