package com.wbrawner.twigs.shared

import com.wbrawner.twigs.shared.budget.Budget
import com.wbrawner.twigs.shared.budget.BudgetRepository

class FakeBudgetRepository: BudgetRepository {
    val budgets = mutableListOf<Budget>()

    override suspend fun getBalance(id: String): Long {
        return 0
    }

    override suspend fun create(newItem: Budget): Budget {
        val saved = newItem.copy(id = randomId())
        budgets.add(saved)
        return saved
    }

    override suspend fun findAll(): List<Budget> = budgets

    override suspend fun findById(id: String): Budget = budgets.first { it.id == id }

    override suspend fun update(updatedItem: Budget): Budget {
        budgets.removeAll { it.id == updatedItem.id }
        budgets.add(updatedItem)
        budgets.sortBy { it.name }
        return updatedItem
    }

    override suspend fun delete(id: String) {
        budgets.removeAll { it.id == id }
    }
}