package com.wbrawner.budget.ui.overview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.common.budget.BudgetRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class OverviewViewModel : ViewModel() {
    val state = MutableLiveData<AsyncState<Long>>(AsyncState.Loading)

    @Inject
    lateinit var budgetRepo: BudgetRepository

    fun loadOverview(id: Long? = null) {
        if (id == null) {
            state.postValue(AsyncState.Error("Invalid Budget ID"))
            return
        }
        viewModelScope.launch {
            state.postValue(AsyncState.Loading)
            try {
                val balance = budgetRepo.getBalance(id)
                state.postValue(AsyncState.Success(balance))
            } catch (e: Exception) {
                state.postValue(AsyncState.Error(e))
            }
        }
    }
}
