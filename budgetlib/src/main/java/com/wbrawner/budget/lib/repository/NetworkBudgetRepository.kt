package com.wbrawner.budget.lib.repository

import android.content.SharedPreferences
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.lib.network.BudgetApiService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicReference

const val KEY_DEFAULT_BUDGET = "defaultBudget"

class NetworkBudgetRepository(
        private val apiService: BudgetApiService,
        private val sharedPreferences: SharedPreferences
) : BudgetRepository {
    private val mutex = Mutex()
    private val budgets: MutableSet<Budget> = mutableSetOf()
    private val currentBudgetRef = AtomicReference<Budget>()
    override var currentBudget: Budget?
        get() = currentBudgetRef.get()
        set(value) {
            currentBudgetRef.set(value)
            sharedPreferences.edit().apply {
                value?.id?.let {
                    putLong(KEY_DEFAULT_BUDGET, it)
                } ?: remove(KEY_DEFAULT_BUDGET)
                apply()
            }
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

    override suspend fun findById(id: Long): Budget = mutex.withLock {
        budgets.firstOrNull { it.id == id }
    } ?: apiService.getBudget(id).apply {
        mutex.withLock {
            budgets.add(this)
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