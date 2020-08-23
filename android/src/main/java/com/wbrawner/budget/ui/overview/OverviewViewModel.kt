package com.wbrawner.budget.ui.overview

import android.util.Log
import androidx.lifecycle.*
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.transaction.TransactionRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class OverviewViewModel : ViewModel() {
    val state = MutableLiveData<AsyncState<OverviewState>>(AsyncState.Loading)

    @Inject
    lateinit var budgetRepo: BudgetRepository

    @Inject
    lateinit var transactionRepo: TransactionRepository

    fun loadOverview(lifecycleOwner: LifecycleOwner) {
        budgetRepo.currentBudget.observe(lifecycleOwner, Observer { budget ->
            if (budget == null) {
                state.postValue(AsyncState.Error("Invalid Budget ID"))
                return@Observer
            }
            viewModelScope.launch {
                state.postValue(AsyncState.Loading)
                try {
                    // TODO: Load expected and actual income/expense amounts as well
                    var balance = 0L
                    transactionRepo.findAll(listOf(budget.id!!)).forEach {
                        Log.d("OverviewViewModel", "${it.title} - ${it.amount}")
                        if (it.expense) {
                            balance -= it.amount
                        } else {
                            balance += it.amount
                        }
                    }
                    state.postValue(AsyncState.Success(OverviewState(
                            budget,
                            balance
                    )))
                } catch (e: Exception) {
                    state.postValue(AsyncState.Error(e))
                }
            }
        })
    }
}

data class OverviewState(
        val budget: Budget,
        val balance: Long
)