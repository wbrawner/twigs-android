package com.wbrawner.budget.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wbrawner.budget.R
import com.wbrawner.budget.ui.auth.LoginScreen
import com.wbrawner.budget.ui.base.TwigsApp
import com.wbrawner.budget.ui.category.CategoriesScreen
import com.wbrawner.budget.ui.category.CategoryDetailsScreen
import com.wbrawner.budget.ui.recurringtransaction.RecurringTransactionDetailsScreen
import com.wbrawner.budget.ui.recurringtransaction.RecurringTransactionsScreen
import com.wbrawner.budget.ui.transaction.TransactionDetailsScreen
import com.wbrawner.budget.ui.transaction.TransactionsScreen
import com.wbrawner.twigs.shared.Route
import com.wbrawner.twigs.shared.Store
import com.wbrawner.twigs.shared.budget.BudgetAction
import com.wbrawner.twigs.shared.category.CategoryAction
import com.wbrawner.twigs.shared.recurringtransaction.RecurringTransactionAction
import com.wbrawner.twigs.shared.transaction.TransactionAction
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var store: Store

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val state by store.state.collectAsState()
            val navController = rememberNavController()
            LaunchedEffect(state.route) {
                navController.navigate(state.route.path)
            }
            TwigsApp {
                val authViewModel: AuthViewModel = hiltViewModel()
                NavHost(navController, state.initialRoute.path) {
                    composable(Route.Login.path) {
                        LoginScreen(store = store, viewModel = authViewModel)
                    }
                    composable(Route.Overview.path) {
                        OverviewScreen(store = store)
                    }
                    composable(Route.Transactions().path) {
                        TransactionsScreen(store = store)
                    }
                    composable(
                        Route.Transactions(selected = "{id}").path,
                        arguments = listOf(navArgument("id") {
                            type = NavType.StringType
                            nullable = false
                        })
                    ) {
                        TransactionDetailsScreen(store = store)
                    }
                    composable(Route.Categories().path) {
                        CategoriesScreen(store = store)
                    }
                    composable(
                        Route.Categories(selected = "{id}").path,
                        arguments = listOf(navArgument("id") {
                            type = NavType.StringType
                            nullable = false
                        })
                    ) {
                        CategoryDetailsScreen(store = store)
                    }
                    composable(Route.RecurringTransactions().path) {
                        RecurringTransactionsScreen(store = store)
                    }
                    composable(
                        Route.RecurringTransactions(selected = "{id}").path,
                        arguments = listOf(navArgument("id") {
                            type = NavType.StringType
                            nullable = false
                        })
                    ) {
                        RecurringTransactionDetailsScreen(store = store)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwigsScaffold(
    store: Store,
    title: String,
    onClickFab: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    navigationIcon: @Composable () -> Unit = {
        val coroutineScope = rememberCoroutineScope()
        IconButton(onClick = {
            coroutineScope.launch {
                drawerState.open()
            }
        }) {
            Icon(Icons.Default.Menu, "Main menu")
        }
    },
    content: @Composable (padding: PaddingValues) -> Unit
) {
    val state by store.state.collectAsState()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                TwigsDrawer(store = store, drawerState::close)
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = navigationIcon,
                    actions = actions,
                    title = {
                        Text(title)
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = state.route == Route.Overview,
                        onClick = { store.dispatch(BudgetAction.OverviewClicked) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                        label = { Text(text = "Overview") }
                    )
                    NavigationBarItem(
                        selected = state.route is Route.Transactions,
                        onClick = { store.dispatch(TransactionAction.TransactionsClicked) },
                        icon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                        label = { Text(text = "Transactions") }
                    )
                    NavigationBarItem(
                        selected = state.route is Route.Categories,
                        onClick = { store.dispatch(CategoryAction.CategoriesClicked) },
                        icon = { Icon(Icons.Default.Category, contentDescription = null) },
                        label = { Text(text = "Categories") }
                    )
                    NavigationBarItem(
                        selected = state.route is Route.RecurringTransactions,
                        onClick = { store.dispatch(RecurringTransactionAction.RecurringTransactionsClicked) },
                        icon = { Icon(Icons.Default.Repeat, contentDescription = null) },
                        label = { Text(text = "Recurring") }
                    )
                }
            },
            floatingActionButton = {
                onClickFab?.let { onClick ->
                    FloatingActionButton(onClick = onClick) {
                        Icon(imageVector = Icons.Default.Add, "Add")
                    }
                }
            }
        ) {
            content(it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwigsDrawer(store: Store, close: suspend () -> Unit) {
    val state by store.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val image =
                if (isSystemInDarkTheme()) R.drawable.ic_twigs_outline else R.drawable.ic_twigs_color
            Image(painter = painterResource(id = image), null)
            Text(
                text = "twigs",
                style = MaterialTheme.typography.titleLarge
            )
        }
        state.budgets?.let { budgets ->
            LazyColumn(modifier = Modifier.fillMaxHeight()) {
                items(budgets) { budget ->
                    val selected = budget.id == state.selectedBudget
                    val icon = if (selected) Icons.Filled.Folder else Icons.Default.Folder
                    NavigationDrawerItem(
                        icon = { Icon(icon, contentDescription = null) },
                        label = { Text(budget.name) },
                        selected = selected,
                        onClick = {
                            store.dispatch(BudgetAction.SelectBudget(budget.id))
                            coroutineScope.launch {
                                close()
                            }
                        }
                    )
                }
            }
        }
    }
}