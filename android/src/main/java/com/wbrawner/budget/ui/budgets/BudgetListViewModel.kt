package com.wbrawner.budget.ui.budgets

import androidx.lifecycle.ViewModel
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.AsyncViewModel
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.load
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class BudgetListViewModel @Inject constructor(
    val budgetRepository: BudgetRepository
) : ViewModel(), AsyncViewModel<List<Budget>> {
    override val state: MutableStateFlow<AsyncState<List<Budget>>> =
        MutableStateFlow(AsyncState.Loading)

    fun getBudgets() {
        load {
            budgetRepository.findAll().toList()
        }
    }
}
