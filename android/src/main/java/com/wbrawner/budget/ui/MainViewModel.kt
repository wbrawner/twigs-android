package com.wbrawner.budget.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.user.UserRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel : ViewModel() {
    @Inject
    lateinit var budgetRepository: BudgetRepository

    @Inject
    lateinit var userRepository: UserRepository

    private val budgets = MutableLiveData<BudgetList>()

    fun loadBudgets(): LiveData<BudgetList> {
        viewModelScope.launch {
            val list = budgetRepository.findAll().sortedBy { it.name }
            budgets.postValue(BudgetList(
                    list,
                    budgetRepository.currentBudget.value?.let {
                        list.indexOf(it)
                    }
            ))
        }
        return budgets
    }

    fun loadBudget(index: Int) {
        val list = budgets.value ?: return
        viewModelScope.launch {
            budgetRepository.findById(list.budgets[index].id!!, true)
            budgets.postValue(list.copy(selectedIndex = index))
        }
    }
}

data class BudgetList(
        val budgets: List<Budget>,
        val selectedIndex: Int? = null
)