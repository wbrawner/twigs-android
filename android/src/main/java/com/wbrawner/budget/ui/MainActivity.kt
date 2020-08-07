package com.wbrawner.budget.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.emoji.text.EmojiCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.wbrawner.budget.R
import kotlinx.android.synthetic.main.activity_transaction_list.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiCompat.init(androidx.emoji.bundled.BundledEmojiCompatConfig(this))
        setContentView(R.layout.activity_transaction_list)
        setSupportActionBar(action_bar)
        val navController = findNavController(R.id.content_container)
        menu_main.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            title = destination.label
            val showHomeAsUp = when (destination.label) {
                getString(R.string.title_overview) -> false
                getString(R.string.title_transactions) -> false
                getString(R.string.title_profile) -> false
                getString(R.string.title_budgets) -> false
                else -> true
            }
            supportActionBar?.setDisplayHomeAsUpEnabled(showHomeAsUp)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController(R.id.content_container).navigateUp()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_OPEN_FRAGMENT = "com.wbrawner.budget.MainActivity.EXTRA_OPEN_FRAGMENT"
    }
}
