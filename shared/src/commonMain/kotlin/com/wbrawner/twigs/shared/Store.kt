package com.wbrawner.twigs.shared

import com.russhwolf.settings.Settings
import com.wbrawner.twigs.shared.budget.Budget
import com.wbrawner.twigs.shared.category.Category
import com.wbrawner.twigs.shared.network.APIService
import com.wbrawner.twigs.shared.transaction.Transaction
import com.wbrawner.twigs.shared.user.User
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class Route(val path: String) {
    WELCOME("welcome"),
    LOGIN("login"),
    REGISTER("register"),
    OVERVIEW("overview"),
    TRANSACTIONS("transactions"),
    CATEGORIES("categories"),
    RECURRING_TRANSACTIONS("recurringtransactions"),
    PROFILE("profile"),
    SETTINGS("settings"),
    ABOUT("about"),
}

data class State(
    val host: String? = null,
    val user: User? = null,
    val budgets: List<Budget>? = null,
    val selectedBudget: String? = null,
    val editingBudget: Boolean = false,
    val categories: List<Category>? = null,
    val selectedCategory: String? = null,
    val editingCategory: Boolean = false,
    val transactions: List<Transaction>? = null,
    val selectedTransaction: String? = null,
    val editingTransaction: Boolean = false,
    val loading: Boolean = false,
    val route: Route = Route.WELCOME,
    val initialRoute: Route = Route.WELCOME
)

interface Action {
    object About : Action
    object Back : Action
}

interface AsyncAction : Action

interface Effect {
    object Exit : Effect
    object Empty: Effect
}

abstract class Reducer {
    lateinit var dispatch: (Action) -> Unit
    lateinit var emit: (Effect) -> Unit

    abstract fun reduce(action: Action, state: State): State
}

abstract class AsyncReducer : Reducer() {
    abstract suspend fun reduce(action: AsyncAction, state: State): State
}

const val KEY_HOST = "baseUrl"

class Store(
    private val reducers: List<Reducer>,
    private val settings: Settings = Settings(),
    initialState: State = State()
) : CoroutineScope by CoroutineScope(Dispatchers.Main) {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state
    private val _effects = MutableSharedFlow<Effect>()
    val effects: Flow<Effect> = _effects

    init {
        reducers.forEach {
            it.dispatch = this::dispatch
        }
    }

    fun dispatch(action: Action) {
        launch {
            var state = _state.value
            if (action is AsyncAction) {
                reducers.filterIsInstance<AsyncReducer>().forEach {
                    state = it.reduce(action, state)
                }
            } else {
                reducers.forEach {
                    state = it.reduce(action, state)
                }
            }
            _state.emit(state)
        }
    }

    companion object
}