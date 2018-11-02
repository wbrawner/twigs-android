package com.wbrawner.budget.ui

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.text.emoji.EmojiCompat
import android.support.text.emoji.bundled.BundledEmojiCompatConfig
import android.support.v7.app.AppCompatActivity
import com.wbrawner.budget.R
import com.wbrawner.budget.ui.categories.CategoryListFragment
import com.wbrawner.budget.ui.overview.OverviewFragment
import com.wbrawner.budget.ui.transactions.TransactionListFragment
import kotlinx.android.synthetic.main.activity_transaction_list.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiCompat.init(BundledEmojiCompatConfig(this))
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
        menu_main.selectedItemId = R.id.action_overview
    }

    private fun updateFragment(tag: String, @StringRes title: Int) {
        setTitle(title)
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        val ft = supportFragmentManager.beginTransaction()
        if (fragment == null) {
            fragment = when (tag) {
                OverviewFragment.TAG_FRAGMENT -> OverviewFragment()
                CategoryListFragment.TAG_FRAGMENT -> CategoryListFragment()
                else -> {
                    TransactionListFragment()
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
