package com.wbrawner.budget.ui.budgets

import androidx.lifecycle.ViewModel
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.AsyncViewModel
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.load
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class BudgetListViewModel : ViewModel(), AsyncViewModel<List<Budget>> {
    override val state: MutableStateFlow<AsyncState<List<Budget>>> = MutableStateFlow(AsyncState.Loading)

    @Inject
    lateinit var budgetRepo: BudgetRepository

    fun getBudgets() {
        load {
            budgetRepo.findAll().toList()
        }
    }
}
