package com.wbrawner.budget.ui.budgets

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.AsyncViewModel
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.launch
import javax.inject.Inject

class BudgetListViewModel : ViewModel(), AsyncViewModel<List<Budget>> {
    override val state: MutableLiveData<AsyncState<List<Budget>>> = MutableLiveData(AsyncState.Loading)

    @Inject
    lateinit var budgetRepo: BudgetRepository

    fun getBudgets() {
        launch {
            budgetRepo.findAll().toList()
        }
    }
}
