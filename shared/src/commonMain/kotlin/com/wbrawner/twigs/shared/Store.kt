package com.wbrawner.twigs.shared

import com.wbrawner.twigs.shared.budget.Budget
import com.wbrawner.twigs.shared.category.Category
import com.wbrawner.twigs.shared.recurringtransaction.RecurringTransaction
import com.wbrawner.twigs.shared.transaction.Transaction
import com.wbrawner.twigs.shared.user.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

sealed class Route(val path: String) {
    object Welcome : Route("welcome")
    object Login : Route("login")
    object Register : Route("register")
    object Overview : Route("overview")
    data class Transactions(val selected: String? = null) :
        Route(if (selected != null) "transactions/${selected}" else "transactions")

    data class Categories(val selected: String? = null) :
        Route(if (selected != null) "categories/${selected}" else "categories")

    data class RecurringTransactions(val selected: String? = null) :
        Route(if (selected != null) "recurringtransactions/${selected}" else "recurringtransactions")

    object Profile : Route("profile")
    object Settings : Route("settings")
    object About : Route("about")
}

data class State(
    val user: User? = null,
    val budgets: List<Budget>? = null,
    val budgetBalance: Long? = null,
    val actualIncome: Long? = null,
    val actualExpenses: Long? = null,
    val expectedIncome: Long? = null,
    val expectedExpenses: Long? = null,
    val selectedBudget: String? = null,
    val editingBudget: Boolean = false,
    val categories: List<Category>? = null,
    val categoryBalances: Map<String, Long>? = null,
    val selectedCategory: String? = null,
    val editingCategory: Boolean = false,
    val transactions: List<Transaction>? = null,
    val selectedTransaction: String? = null,
    val selectedTransactionCreatedBy: User? = null,
    val editingTransaction: Boolean = false,
    val from: Instant = startOfMonth(),
    val to: Instant = endOfMonth(),
    val recurringTransactions: List<RecurringTransaction>? = null,
    val selectedRecurringTransaction: String? = null,
    val selectedRecurringTransactionCreatedBy: User? = null,
    val editingRecurringTransaction: Boolean = false,
    val loading: Boolean = false,
    val route: Route = Route.Login,
    val initialRoute: Route = Route.Login
)

interface Action {
    object AboutClicked : Action
    object Back : Action
}

interface Effect {
    object Exit : Effect
    data class Error(val message: String) : Effect
    object Empty: Effect
}

abstract class Reducer : CoroutineScope by CoroutineScope(Dispatchers.Main) {
    lateinit var dispatch: (Action) -> Unit
    lateinit var emit: (Effect) -> Unit
    internal val initialActions = ArrayDeque<Action>()

    abstract fun reduce(action: Action, state: () -> State): State
}

class Store(
    private val reducers: List<Reducer>,
    initialState: State = State()
) : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state
    private val _effects = MutableSharedFlow<Effect>()
    val effects: Flow<Effect> = _effects

    init {
        reducers.forEach {
            it.dispatch = this::dispatch
            it.emit = {
                launch {
                    _effects.emit(it)
                }
            }
            var action = it.initialActions.removeFirstOrNull()
            while (action != null) {
                dispatch(action)
                action = it.initialActions.removeFirstOrNull()
            }
        }
        launch {
            state.collect {
                println(it)
            }
        }
    }

    fun dispatch(action: Action) {
        launch {
            var newState = _state.value
            reducers.forEach {
                newState = it.reduce(action) { newState }
            }
            _state.emit(newState)
        }
    }

    companion object
}