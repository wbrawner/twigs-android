package com.wbrawner.twigs.shared

import com.russhwolf.settings.Settings
import com.wbrawner.twigs.shared.budget.BudgetAction
import com.wbrawner.twigs.shared.budget.BudgetReducer
import com.wbrawner.twigs.shared.user.Permission
import com.wbrawner.twigs.shared.user.UserPermission
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BudgetReducerTests {
    lateinit var reducer: BudgetReducer
    lateinit var dispatchedActions: MutableList<Action>
    lateinit var budgetRepository: FakeBudgetRepository
    lateinit var settings: Settings

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(newSingleThreadContext("main"))
        dispatchedActions = mutableListOf()
        budgetRepository = FakeBudgetRepository()
        settings = FakeSettings()
        reducer = BudgetReducer(budgetRepository, settings)
        reducer.dispatch = { dispatchedActions.add(it) }
    }

    @Test
    fun goBackWhileEditingTest() {
        val state = State(editingBudget = true, selectedBudget = "test")
        val newState = reducer.reduce(Action.Back) { state }
        assertFalse(newState.editingBudget)
        assertEquals("selectedBudget should still be set", "test", newState.selectedBudget)
    }

    @Test
    fun createBudgetTest() {
        val state = State()
        val users = listOf(UserPermission("user", Permission.OWNER))
        assertFalse(state.loading)
        val newState = reducer.reduce(BudgetAction.CreateBudget("test", "description", users)) { state }
        assertTrue(newState.loading)
        assertNull(newState.selectedBudget)
    }
}