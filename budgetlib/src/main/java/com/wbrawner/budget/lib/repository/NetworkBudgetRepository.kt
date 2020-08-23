package com.wbrawner.budget.lib.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.lib.network.BudgetApiService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

const val KEY_DEFAULT_BUDGET = "defaultBudget"

class NetworkBudgetRepository(
        private val apiService: BudgetApiService,
        private val sharedPreferences: SharedPreferences
) : BudgetRepository {
    private val mutex = Mutex()
    private val budgets: MutableSet<Budget> = mutableSetOf()
    override val currentBudget: LiveData<Budget?> = MutableLiveData()

    init {
        currentBudget.observeForever { budget ->
            sharedPreferences.edit().apply {
                budget?.id?.let {
                    putLong(KEY_DEFAULT_BUDGET, it)
                } ?: remove(KEY_DEFAULT_BUDGET)
                apply()
            }
        }
        GlobalScope.launch {
            prefetchData()
        }
    }

    override suspend fun prefetchData() {
        val budgets = try {
            findAll()
        } catch (e: Exception) {
            emptyList<Budget>()
        }
        if (budgets.isEmpty()) return
        val budgetId = sharedPreferences.getLong(KEY_DEFAULT_BUDGET, budgets.first().id!!)
        val budget = try {
            findById(budgetId)
        } catch (e: Exception) {
            // For some reason we can't find the default budget id, so fallback to the first budget
            budgets.first()
        }
        (currentBudget as MutableLiveData).postValue(budget)
    }

    override suspend fun create(newItem: Budget): Budget =
            apiService.newBudget(NewBudgetRequest(newItem)).apply {
                mutex.withLock {
                    budgets.add(this)
                }
            }

    override suspend fun findAll(): List<Budget> = mutex.withLock {
        if (budgets.size > 0) budgets.toList() else null
    } ?: apiService.getBudgets().sortedBy { it.name }.apply {
        mutex.withLock {
            budgets.addAll(this)
        }
    }

    override suspend fun findById(id: Long, setCurrent: Boolean): Budget = (mutex.withLock {
        budgets.firstOrNull { it.id == id }
    } ?: apiService.getBudget(id).apply {
        mutex.withLock {
            budgets.add(this)
        }
    }).apply {
        if (setCurrent) {
            (currentBudget as MutableLiveData).postValue(this)
        }
    }

    override suspend fun update(updatedItem: Budget): Budget =
            apiService.updateBudget(updatedItem.id!!, updatedItem).apply {
                mutex.withLock {
                    budgets.removeAll { it.id == this.id }
                    budgets.add(this)
                }
            }

    override suspend fun delete(id: Long) = apiService.deleteBudget(id).apply {
        mutex.withLock {
            budgets.removeAll { it.id == id }
        }
    }

    override suspend fun getBalance(id: Long): Long = apiService.getBudgetBalance(id).balance
}

data class NewBudgetRequest(
        val name: String,
        val description: String? = null,
        val userIds: List<Long>
) {
    constructor(budget: Budget) : this(budget.name, budget.description, budget.users.map { it.id!! })
}