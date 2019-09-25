package com.wbrawner.budget.ui.budgets

import androidx.lifecycle.ViewModel
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.di.ViewModelKey
import com.wbrawner.budget.ui.base.LoadingViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject

class BudgetViewModel @Inject constructor(private val budgetRepo: BudgetRepository) : LoadingViewModel() {
    suspend fun getBudget(id: Long): Budget = showLoader {
        budgetRepo.findById(id)
    }

    suspend fun getBudgets(): Collection<Budget> = showLoader {
        budgetRepo.findAll()
    }

    suspend fun saveBudget(budget: Budget) = showLoader {
        if (budget.id == null)
            budgetRepo.create(budget)
        else
            budgetRepo.update(budget)
    }

    suspend fun deleteBudgetById(id: Long) = showLoader {
        budgetRepo.delete(id)
    }

    suspend fun getBalance(id: Long) = showLoader {
        budgetRepo.getBalance(id)
    }
}

@Module
abstract class BudgetViewModelMapper {
    @Binds
    @IntoMap
    @ViewModelKey(BudgetViewModel::class)
    abstract fun bindViewModel(viewModel: BudgetViewModel): ViewModel
}