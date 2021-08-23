package com.wbrawner.budget.lib.repository

import android.content.SharedPreferences
import com.wbrawner.budget.common.budget.Budget
import com.wbrawner.budget.common.budget.BudgetRepository
import com.wbrawner.budget.common.user.UserPermission
import com.wbrawner.budget.lib.network.TwigsApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

const val KEY_DEFAULT_BUDGET = "defaultBudget"

class NetworkBudgetRepository(
    private val apiService: TwigsApiService,
    private val sharedPreferences: SharedPreferences
) : BudgetRepository {
    private val mutex = Mutex()
    private val budgets: MutableSet<Budget> = mutableSetOf()
    private val current = MutableStateFlow<Budget?>(null)
    override val currentBudget: SharedFlow<Budget?> = current.asSharedFlow()

    init {
        currentBudget.onEach { budget ->
            sharedPreferences.edit().apply {
                budget?.id?.let {
                    putString(KEY_DEFAULT_BUDGET, it)
                } ?: remove(KEY_DEFAULT_BUDGET)
                apply()
            }
        }
    }

    override suspend fun prefetchData() {
        val budgets = try {
            findAll()
        } catch (e: Exception) {
            emptyList()
        }
        if (budgets.isEmpty()) return
        val budgetId = sharedPreferences.getString(KEY_DEFAULT_BUDGET, budgets.first().id)!!
        val budget = try {
            findById(budgetId)
        } catch (e: Exception) {
            // For some reason we can't find the default budget id, so fallback to the first budget
            budgets.first()
        }
        current.emit(budget)
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

    override suspend fun findById(id: String, setCurrent: Boolean): Budget = (mutex.withLock {
        budgets.firstOrNull { it.id == id }
    } ?: apiService.getBudget(id).apply {
        mutex.withLock {
            budgets.add(this)
        }
    }).apply {
        if (setCurrent) {
            current.emit(this)
        }
    }

    override suspend fun update(updatedItem: Budget): Budget =
        apiService.updateBudget(updatedItem.id!!, updatedItem).apply {
            mutex.withLock {
                budgets.removeAll { it.id == this.id }
                budgets.add(this)
            }
        }

    override suspend fun delete(id: String) = apiService.deleteBudget(id).apply {
        mutex.withLock {
            budgets.removeAll { it.id == id }
        }
    }

    override suspend fun getBalance(id: String): Long =
        apiService.sumTransactions(budgetId = id).balance
}

data class NewBudgetRequest(
    val name: String,
    val description: String? = null,
    val users: List<UserPermission>
) {
    constructor(budget: Budget) : this(
        budget.name,
        budget.description,
        budget.users
    )
}