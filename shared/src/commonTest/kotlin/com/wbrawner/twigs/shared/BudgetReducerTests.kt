package com.wbrawner.twigs.shared

import com.wbrawner.twigs.shared.budget.BudgetAction
import com.wbrawner.twigs.shared.budget.BudgetReducer
import com.wbrawner.twigs.shared.budget.BudgetRepository
import com.wbrawner.twigs.shared.user.Permission
import com.wbrawner.twigs.shared.user.UserPermission
import kotlinx.datetime.toInstant
import kotlin.test.BeforeTest
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.DefaultAsserter.assertTrue
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull

class BudgetReducerTests {
    lateinit var reducer: BudgetReducer
    lateinit var dispatchedActions: MutableList<Action>
    lateinit var budgetRepository: FakeBudgetRepository

    @BeforeTest
    fun setup() {
        dispatchedActions = mutableListOf()
        budgetRepository = FakeBudgetRepository()
        reducer = BudgetReducer(budgetRepository)
        reducer.dispatch = { dispatchedActions.add(it) }
    }

    @Test
    fun goBackWhileEditingTest() {
        val state = State(editingBudget = true, selectedBudget = "test")
        val newState = reducer.reduce(Action.Back, state)
        assertFalse(newState.editingBudget)
        assertEquals("selectedBudget should still be set", "test", newState.selectedBudget)
    }

    @Test
    fun goBackWhileViewingTest() {
        val state = State(selectedBudget = "test")
        val newState = reducer.reduce(Action.Back, state)
        assertNull(newState.selectedBudget)
    }

    @Test
    fun createBudgetTest() {
        val state = State()
        val users = listOf(UserPermission("user", Permission.OWNER))
        assertFalse(state.loading)
        val newState = reducer.reduce(BudgetAction.CreateBudget("test", "description", users), state)
        assertTrue(state.loading)

        assertNull(newState.selectedBudget)
    }
}