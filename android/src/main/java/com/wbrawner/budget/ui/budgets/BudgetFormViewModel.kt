package com.wbrawner.budget.ui.budgets

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wbrawner.budget.R
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.user.User
import com.wbrawner.budget.common.user.UserRepository
import com.wbrawner.budget.common.util.randomId
import kotlinx.coroutines.launch
import javax.inject.Inject

class BudgetFormViewModel : ViewModel() {

    val state = MutableLiveData<BudgetFormState>(BudgetFormState.Loading)
    val users = MutableLiveData<List<User>>()
    val userSuggestions = MutableLiveData<List<User>>()

    @Inject
    lateinit var budgetRepository: BudgetRepository

    @Inject
    lateinit var userRepository: UserRepository

    fun getBudget(id: String? = null) {
        viewModelScope.launch {
            state.postValue(BudgetFormState.Loading)
            try {
                val budget = id?.let {
                    budgetRepository.findById(it)
                } ?: Budget(name = "")
                state.postValue(BudgetFormState.Success(budget))
            } catch (e: Exception) {
                state.postValue(BudgetFormState.Failed(e))
            }
        }
    }

    fun saveBudget(budget: Budget) {
        viewModelScope.launch {
            state.postValue(BudgetFormState.Loading)
            try {
                if (budget.id != null) {
                    budgetRepository.update(budget)
                } else {
                    budgetRepository.create(budget.copy(id = randomId()))
                }
                state.postValue(BudgetFormState.Exit)
            } catch (e: Exception) {
                state.postValue(BudgetFormState.Failed(e))
            }
        }
    }

    fun deleteBudget(budgetId: String) {
        viewModelScope.launch {
            state.postValue(BudgetFormState.Loading)
            try {
                budgetRepository.delete(budgetId)
                state.postValue(BudgetFormState.Exit)
            } catch (e: Exception) {
                state.postValue(BudgetFormState.Failed(e))
            }
        }
    }

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            userSuggestions.value = emptyList()
            return
        }
        viewModelScope.launch {
            userSuggestions.value = userRepository.findAllByNameLike(query).toList()
        }
    }

    fun addUser(user: User) {
        users.value
    }
}

sealed class BudgetFormState {
    object Loading: BudgetFormState()
    class Success(
            @StringRes val titleRes: Int,
            val showDeleteButton: Boolean,
            val budget: Budget
    ): BudgetFormState() {
        constructor(budget: Budget): this(
                budget.id?.let { R.string.title_edit_budget }?: R.string.title_add_budget,
                budget.id != null,
                budget
        )
    }
    class Failed(val exception: Exception): BudgetFormState()
    object Exit: BudgetFormState()
}
