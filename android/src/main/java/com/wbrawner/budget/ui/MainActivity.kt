package com.wbrawner.budget.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.emoji.text.EmojiCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.wbrawner.budget.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val MENU_GROUP_BUDGETS = 50
private const val MENU_ITEM_ADD_BUDGET = 100
private const val MENU_ITEM_SETTINGS = 101

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var toggle: ActionBarDrawerToggle
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EmojiCompat.init(androidx.emoji.bundled.BundledEmojiCompatConfig(this))
        setContentView(R.layout.activity_main)
        setSupportActionBar(action_bar)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.action_open, R.string.action_close)
        toggle.isDrawerIndicatorEnabled = true
        toggle.isDrawerSlideAnimationEnabled = true
        drawerLayout.addDrawerListener(toggle)
        navigationView.setNavigationItemSelectedListener(this)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navController = findNavController(R.id.content_container)
        menu_main.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            title = destination.label
            val homeAsUpIndicator = when (destination.label) {
                getString(R.string.title_overview) -> R.drawable.ic_menu
                getString(R.string.title_transactions) -> R.drawable.ic_menu
                getString(R.string.title_profile) -> R.drawable.ic_menu
                getString(R.string.title_categories) -> R.drawable.ic_menu
                else -> 0
            }
            supportActionBar?.setHomeAsUpIndicator(homeAsUpIndicator)
        }
        lifecycleScope.launch {
            viewModel.loadBudgets().collect { list ->
                val menu = navigationView.menu
                menu.clear()
                val budgetsMenu = navigationView.menu.addSubMenu(0, 0, 0, "Budgets")
                list.budgets.forEachIndexed { index, budget ->
                    budgetsMenu.add(MENU_GROUP_BUDGETS, index, index, budget.name)
                        .setIcon(R.drawable.ic_folder_selectable)
                }
                budgetsMenu.setGroupCheckable(MENU_GROUP_BUDGETS, true, true)
                list.selectedIndex?.let {
                    budgetsMenu.getItem(it).isChecked = true
                }
                menu.add(0, MENU_ITEM_ADD_BUDGET, list.budgets.size, R.string.title_add_budget)
                    .setIcon(R.drawable.ic_add_white_24dp)
                menu.add(1, MENU_ITEM_SETTINGS, list.budgets.size + 1, "Settings")
                    .setIcon(R.drawable.ic_settings)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId != android.R.id.home) return super.onOptionsItemSelected(item)
        with(findNavController(R.id.content_container)) {
            when (currentDestination?.label) {
                getString(R.string.title_overview) -> drawerLayout.open()
                getString(R.string.title_transactions) -> drawerLayout.open()
                getString(R.string.title_profile) -> drawerLayout.open()
                getString(R.string.title_categories) -> drawerLayout.open()
                else -> navigateUp()
            }
        }
        return true
    }

    companion object {
        const val EXTRA_OPEN_FRAGMENT = "com.wbrawner.budget.MainActivity.EXTRA_OPEN_FRAGMENT"
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            MENU_ITEM_ADD_BUDGET -> findNavController(R.id.content_container).navigate(R.id.addEditBudget)
            MENU_ITEM_SETTINGS -> findNavController(R.id.content_container).navigate(R.id.addEditBudget)
            else -> viewModel.loadBudget(item.itemId)
        }
        drawerLayout.close()
        return true
    }
}
