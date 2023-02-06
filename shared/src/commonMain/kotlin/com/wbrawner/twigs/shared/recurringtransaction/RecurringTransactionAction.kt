package com.wbrawner.twigs.shared.recurringtransaction

import com.wbrawner.twigs.shared.Action
import com.wbrawner.twigs.shared.Effect
import com.wbrawner.twigs.shared.Reducer
import com.wbrawner.twigs.shared.Route
import com.wbrawner.twigs.shared.State
import com.wbrawner.twigs.shared.budget.Budget
import com.wbrawner.twigs.shared.budget.BudgetAction
import com.wbrawner.twigs.shared.category.Category
import com.wbrawner.twigs.shared.user.ConfigAction
import com.wbrawner.twigs.shared.user.User
import com.wbrawner.twigs.shared.user.UserPermission
import com.wbrawner.twigs.shared.user.UserRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

sealed interface RecurringTransactionAction : Action {
    object RecurringTransactionsClicked : RecurringTransactionAction
    data class LoadRecurringTransactionsSuccess(val transactions: List<RecurringTransaction>) :
        RecurringTransactionAction

    data class LoadRecurringTransactionsFailed(val error: Exception) : RecurringTransactionAction
    object NewRecurringTransactionClicked : RecurringTransactionAction
    data class CreateRecurringTransaction(
        val title: String,
        val description: String? = null,
        val amount: Long,
        val frequency: Frequency,
        val start: Instant,
        val end: Instant? = null,
        val expense: Boolean,
        val category: Category? = null,
        val budget: Budget,
    ) : RecurringTransactionAction

    data class SaveRecurringTransactionSuccess(val transaction: RecurringTransaction) :
        RecurringTransactionAction

    data class SaveRecurringTransactionFailure(
        val id: String? = null,
        val name: String,
        val description: String? = null,
        val users: List<UserPermission> = emptyList(),
        val error: Exception
    ) : RecurringTransactionAction

    object CancelEditRecurringTransaction : RecurringTransactionAction

    data class EditRecurringTransaction(val id: String) : RecurringTransactionAction

    data class SelectRecurringTransaction(val id: String?) : RecurringTransactionAction

    data class RecurringTransactionSelected(
        val transaction: RecurringTransaction,
        val createdBy: User
    ) :
        RecurringTransactionAction

    data class UpdateRecurringTransaction(
        val id: String,
        val title: String,
        val description: String? = null,
        val amount: Long,
        val frequency: Frequency,
        val start: Instant,
        val end: Instant? = null,
        val expense: Boolean,
        val category: Category? = null,
        val budget: Budget,
    ) : RecurringTransactionAction

    data class DeleteRecurringTransaction(val id: String) : RecurringTransactionAction
}

class RecurringTransactionReducer(
    private val recurringTransactionRepository: RecurringTransactionRepository,
    private val userRepository: UserRepository,
) : Reducer() {
    override fun reduce(action: Action, state: () -> State): State = when (action) {
        is Action.Back -> {
            val currentState = state()
            currentState.copy(
                editingRecurringTransaction = false,
                selectedRecurringTransaction = if (currentState.editingRecurringTransaction) currentState.selectedRecurringTransaction else null,
                route = if (currentState.route is Route.RecurringTransactions && !currentState.route.selected.isNullOrBlank() && !currentState.editingRecurringTransaction) {
                    Route.RecurringTransactions()
                } else {
                    currentState.route
                }
            )
        }

        is RecurringTransactionAction.RecurringTransactionsClicked -> state().copy(
            route = Route.RecurringTransactions(
                null
            )
        )

        is RecurringTransactionAction.LoadRecurringTransactionsSuccess -> state().copy(
            recurringTransactions = action.transactions
        )

        is RecurringTransactionAction.LoadRecurringTransactionsFailed -> state().copy(loading = false)
            .also {
                emit(Effect.Error(action.error.message ?: "Failed to load recurring transactions"))
            }

        is RecurringTransactionAction.NewRecurringTransactionClicked -> state().copy(
            editingRecurringTransaction = true
        )

        is RecurringTransactionAction.CancelEditRecurringTransaction -> {
            val currentState = state()
            currentState.copy(
                editingRecurringTransaction = false,
                selectedRecurringTransaction = if (currentState.route is Route.RecurringTransactions && !currentState.route.selected.isNullOrBlank()) {
                    currentState.selectedRecurringTransaction
                } else {
                    null
                }
            )
        }

        is RecurringTransactionAction.CreateRecurringTransaction -> {
            launch {
                val transaction = recurringTransactionRepository.create(
                    RecurringTransaction(
                        title = action.title,
                        description = action.description,
                        amount = action.amount,
                        frequency = action.frequency,
                        start = action.start,
                        finish = action.end,
                        expense = action.expense,
                        categoryId = action.category?.id,
                        budgetId = action.budget.id!!,
                        createdBy = state().user!!.id!!
                    )
                )
                dispatch(RecurringTransactionAction.SaveRecurringTransactionSuccess(transaction))
            }
            state().copy(loading = true)
        }

        is RecurringTransactionAction.UpdateRecurringTransaction -> {
            val createdBy = state().selectedRecurringTransactionCreatedBy!!
            launch {
                val transaction = recurringTransactionRepository.update(
                    RecurringTransaction(
                        id = action.id,
                        title = action.title,
                        description = action.description,
                        amount = action.amount,
                        frequency = action.frequency,
                        start = action.start,
                        finish = action.end,
                        expense = action.expense,
                        categoryId = action.category?.id,
                        budgetId = action.budget.id!!,
                        createdBy = createdBy.id!!
                    )
                )
                dispatch(RecurringTransactionAction.SaveRecurringTransactionSuccess(transaction))
            }
            state().copy(loading = true)
        }

        is RecurringTransactionAction.SaveRecurringTransactionSuccess -> {
            val currentState = state()
            val transactions =
                currentState.recurringTransactions?.toMutableList() ?: mutableListOf()
            transactions.removeAll { it.id == action.transaction.id }
            transactions.add(action.transaction)
            transactions.sortBy { it.title }
            currentState.copy(
                loading = false,
                recurringTransactions = transactions.toList(),
                selectedRecurringTransaction = action.transaction.id,
                selectedRecurringTransactionCreatedBy = currentState.user,
                editingRecurringTransaction = false
            )
        }

        is BudgetAction.BudgetSelected -> {
            launch {
                try {
                    val transactions = recurringTransactionRepository.findAll(budgetId = action.id)
                    dispatch(
                        RecurringTransactionAction.LoadRecurringTransactionsSuccess(
                            transactions
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    dispatch(RecurringTransactionAction.LoadRecurringTransactionsFailed(e))
                }
            }
            state().copy(recurringTransactions = null)
        }

        is ConfigAction.Logout -> state().copy(
            transactions = null,
            selectedRecurringTransaction = null,
            editingRecurringTransaction = false
        )

        is RecurringTransactionAction.EditRecurringTransaction -> state().copy(
            editingRecurringTransaction = true,
            selectedRecurringTransaction = action.id
        )

        is RecurringTransactionAction.SelectRecurringTransaction -> {
            launch {
                val currentState = state()
                val transaction = currentState.recurringTransactions!!.first { it.id == action.id }
                val createdBy = userRepository.findById(transaction.createdBy)
                dispatch(
                    RecurringTransactionAction.RecurringTransactionSelected(
                        transaction,
                        createdBy
                    )
                )
            }
            state().copy(
                loading = true,
                selectedRecurringTransaction = action.id,
                route = Route.RecurringTransactions(action.id)
            )
        }

        is RecurringTransactionAction.RecurringTransactionSelected -> state().copy(
            selectedRecurringTransaction = action.transaction.id,
            selectedRecurringTransactionCreatedBy = action.createdBy
        )

        else -> state()
    }
}

fun List<RecurringTransaction>.groupByStatus(): Map<String, List<RecurringTransaction>> {
    val thisMonth = mutableListOf<RecurringTransaction>()
    val future = mutableListOf<RecurringTransaction>()
    val expired = mutableListOf<RecurringTransaction>()
    forEach { transaction ->
        if (transaction.isThisMonth) {
            println("Adding ${transaction.title} to this month. end=${transaction.finish}")
            thisMonth.add(transaction)
        } else if (!transaction.isExpired) {
            println("Adding ${transaction.title} to future. end=${transaction.finish}")
            future.add(transaction)
        } else {
            println("Adding ${transaction.title} to expired. end=${transaction.finish}")
            expired.add(transaction)
        }
    }
    val groups = mutableMapOf<String, List<RecurringTransaction>>()
    if (thisMonth.isNotEmpty()) {
        groups["This Month"] = thisMonth.sortedBy { it.title }
    }
    if (future.isNotEmpty()) {
        groups["Future"] = future.sortedBy { it.title }
    }
    if (expired.isNotEmpty()) {
        groups["Expired"] = expired.sortedBy { it.title }
    }
    return groups
}