package com.wbrawner.budget.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val budgetRepository: BudgetRepository,
    val userRepository: UserRepository
) : ViewModel() {

    private val budgets = MutableSharedFlow<BudgetList>(replay = 1)

    fun loadBudgets(): SharedFlow<BudgetList> {
        viewModelScope.launch {
            val list = budgetRepository.findAll().sortedBy { it.name }
            budgets.emit(
                BudgetList(
                    list,
                    budgetRepository.currentBudget.replayCache.firstOrNull()?.let {
                        list.indexOf(it)
                    }
                ))
        }
        return budgets
    }

    fun loadBudget(index: Int) {
        val list = budgets.replayCache.firstOrNull() ?: return
        viewModelScope.launch {
            budgetRepository.findById(list.budgets[index].id!!, true)
            budgets.emit(list.copy(selectedIndex = index))
        }
    }
}

data class BudgetList(
        val budgets: List<Budget> = emptyList(),
        val selectedIndex: Int? = null
)