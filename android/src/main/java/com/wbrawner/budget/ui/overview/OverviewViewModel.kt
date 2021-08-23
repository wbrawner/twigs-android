package com.wbrawner.budget.ui.overview

import androidx.lifecycle.*
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.transaction.TransactionRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class OverviewViewModel : ViewModel() {
    val state = MutableLiveData<AsyncState<OverviewState>>(AsyncState.Loading)

    @Inject
    lateinit var budgetRepo: BudgetRepository

    @Inject
    lateinit var transactionRepo: TransactionRepository

    fun loadOverview() {
        viewModelScope.launch {
            budgetRepo.currentBudget.collect { budget ->
                if (budget == null) {
                    state.postValue(AsyncState.Error("Invalid Budget ID"))
                    return@collect
                }
                viewModelScope.launch {
                    state.postValue(AsyncState.Loading)
                    try {
                        // TODO: Load expected and actual income/expense amounts as well
                        state.postValue(
                            AsyncState.Success(
                                OverviewState(
                                    budget,
                                    budgetRepo.getBalance(budget.id!!)
                                )
                            )
                        )
                    } catch (e: Exception) {
                        state.postValue(AsyncState.Error(e))
                    }
                }
            }
        }
    }
}

data class OverviewState(
    val budget: Budget,
    val balance: Long
)