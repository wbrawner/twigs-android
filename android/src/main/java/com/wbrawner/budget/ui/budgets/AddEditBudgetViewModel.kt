package com.wbrawner.budget.ui.budgets

import android.content.Context
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.user.User
import com.wbrawner.budget.common.user.UserRepository
import com.wbrawner.budget.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddEditBudgetViewModel @Inject constructor(
        context: Context,
        private val userRepository: UserRepository,
        private val budgetRepository: BudgetRepository
) : ViewModel() {
    val suggestionsAdapter = ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_item,
            mutableListOf()
    )
    val users = MutableLiveData<List<User>>()

    suspend fun getBudget(id: Long) = budgetRepository.findById(id)

    suspend fun saveBudget(budget: Budget): Budget = if (budget.id != null) {
        budgetRepository.update(budget)
    } else {
        budgetRepository.create(budget)
    }

    suspend fun deleteBudget(accountId: Long) = budgetRepository.delete(accountId)

    suspend fun searchUsers(query: String) {
        suggestionsAdapter.clear()
        if (query.isNotBlank()) {
            suggestionsAdapter.addAll(userRepository.findAllByNameLike(query).map { it.username })
        }
    }

    fun addUser(user: User) {
        users.value
    }
}

@Module
abstract class AddEditAccountsViewModelMapper {
    @Binds
    @IntoMap
    @ViewModelKey(AddEditBudgetViewModel::class)
    abstract fun bindViewModel(viewModel: AddEditBudgetViewModel): ViewModel
}

fun ViewModel.launch(block: suspend CoroutineScope.() -> Unit) {
    viewModelScope.launch(block = block)
}
