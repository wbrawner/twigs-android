package com.wbrawner.budget.ui.overview

import androidx.lifecycle.*
import com.wbrawner.budget.AsyncState
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.category.CategoryRepository
import com.wbrawner.budget.common.transaction.TransactionRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class OverviewViewModel : ViewModel() {
    val state = MutableLiveData<AsyncState<OverviewState>>(AsyncState.Loading)

    @Inject
    lateinit var budgetRepo: BudgetRepository

    @Inject
    lateinit var categoryRepo: CategoryRepository

    @Inject
    lateinit var transactionRepo: TransactionRepository

    fun loadOverview(lifecycleOwner: LifecycleOwner) {
        budgetRepo.currentBudget.observe(lifecycleOwner, Observer { budget ->
            val budgetId = budget?.id
            if (budgetId == null) {
                state.postValue(AsyncState.Error("Invalid Budget ID"))
                return@Observer
            }
            viewModelScope.launch {
                state.postValue(AsyncState.Loading)
                try {
                    // TODO: Load expected and actual income/expense amounts as well
                    var expectedExpenses = 0L
                    var expectedIncome = 0L
                    var actualExpenses = 0L
                    var actualIncome = 0L
                    categoryRepo.findAll(arrayOf(budgetId)).forEach { category ->
                        val categoryId = category.id ?: return@forEach
                        val balance = categoryRepo.getBalance(categoryId)
                        if (category.expense) {
                            expectedExpenses += category.amount
                            actualExpenses += (balance * -1)
                        } else {
                            expectedIncome += category.amount
                            actualIncome += balance
                        }
                    }
                    state.postValue(AsyncState.Success(OverviewState(
                            budget,
                            budgetRepo.getBalance(budgetId),
                            expectedIncome,
                            expectedExpenses,
                            actualIncome,
                            actualExpenses
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
        val balance: Long,
        val expectedIncome: Long,
        val expectedExpenses: Long,
        val actualIncome: Long,
        val actualExpenses: Long,
)