package com.wbrawner.twigs.shared.transaction

import com.wbrawner.twigs.shared.Action
import com.wbrawner.twigs.shared.Effect
import com.wbrawner.twigs.shared.Reducer
import com.wbrawner.twigs.shared.Route
import com.wbrawner.twigs.shared.State
import com.wbrawner.twigs.shared.budget.Budget
import com.wbrawner.twigs.shared.budget.BudgetAction
import com.wbrawner.twigs.shared.category.Category
import com.wbrawner.twigs.shared.replace
import com.wbrawner.twigs.shared.user.ConfigAction
import com.wbrawner.twigs.shared.user.User
import com.wbrawner.twigs.shared.user.UserPermission
import com.wbrawner.twigs.shared.user.UserRepository
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

sealed interface TransactionAction : Action {
    object TransactionsClicked : TransactionAction
    data class LoadTransactionsSuccess(val transactions: List<Transaction>) : TransactionAction
    data class LoadTransactionsFailed(val error: Exception) : TransactionAction
    data class ChangeDateRange(val from: Instant, val to: Instant) : TransactionAction
    object NewTransactionClicked : TransactionAction
    data class CreateTransaction(
        val title: String,
        val description: String? = null,
        val amount: Long,
        val date: Instant,
        val expense: Boolean,
        val category: Category? = null,
        val budget: Budget,
    ) : TransactionAction

    data class SaveTransactionSuccess(val transaction: Transaction) : TransactionAction

    data class SaveTransactionFailure(
        val id: String? = null,
        val name: String,
        val description: String? = null,
        val users: List<UserPermission> = emptyList(),
        val error: Exception
    ) : TransactionAction

    object CancelEditTransaction : TransactionAction

    data class EditTransaction(val id: String) : TransactionAction

    data class SelectTransaction(val id: String?) : TransactionAction

    data class TransactionSelected(val transaction: Transaction, val createdBy: User) :
        TransactionAction

    data class UpdateTransaction(
        val id: String,
        val title: String,
        val description: String? = null,
        val amount: Long,
        val date: Instant,
        val expense: Boolean,
        val category: Category? = null,
        val budget: Budget,
    ) : TransactionAction

    data class DeleteTransaction(val id: String) : TransactionAction

    data class TransactionDeleted(val id: String) : TransactionAction

    data class TransactionDeletedFailure(val id: String) : TransactionAction
}

class TransactionReducer(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
) : Reducer() {
    override fun reduce(action: Action, state: () -> State): State = when (action) {
        is Action.Back -> {
            val currentState = state()
            currentState.copy(
                editingTransaction = false,
                selectedTransaction = if (currentState.editingTransaction) currentState.selectedTransaction else null,
                route = if (currentState.route is Route.Transactions && !currentState.route.selected.isNullOrBlank() && !currentState.editingTransaction) {
                    Route.Transactions()
                } else {
                    currentState.route
                }
            )
        }

        is TransactionAction.TransactionsClicked -> state().copy(route = Route.Transactions(null))
        is TransactionAction.LoadTransactionsSuccess -> state().copy(transactions = action.transactions)
        is TransactionAction.ChangeDateRange -> state().copy(
            from = action.from,
            to = action.to
        ).also {
            launch {
                try {
                    val transactions = transactionRepository.findAll(
                        budgetIds = listOf(it.selectedBudget!!),
                        start = it.from,
                        end = it.to,
                    )
                    dispatch(TransactionAction.LoadTransactionsSuccess(transactions))
                } catch (e: Exception) {
                    dispatch(TransactionAction.LoadTransactionsFailed(e))
                }
            }
        }

        is TransactionAction.NewTransactionClicked -> state().copy(editingTransaction = true)
        is TransactionAction.CancelEditTransaction -> {
            val currentState = state()
            currentState.copy(
                editingTransaction = false,
                selectedTransaction = if (currentState.route is Route.Transactions && !currentState.route.selected.isNullOrBlank()) {
                    currentState.selectedTransaction
                } else {
                    null
                }
            )
        }

        is TransactionAction.CreateTransaction -> {
            launch {
                val transaction = transactionRepository.create(
                    Transaction(
                        title = action.title,
                        description = action.description,
                        amount = action.amount,
                        date = action.date,
                        expense = action.expense,
                        categoryId = action.category?.id,
                        budgetId = action.budget.id!!,
                        createdBy = state().user!!.id!!
                    )
                )
                dispatch(TransactionAction.SaveTransactionSuccess(transaction))
            }
            state().copy(loading = true)
        }

        is TransactionAction.UpdateTransaction -> {
            val createdBy = state().selectedTransactionCreatedBy!!
            launch {
                val transaction = transactionRepository.update(
                    Transaction(
                        id = action.id,
                        title = action.title,
                        description = action.description,
                        amount = action.amount,
                        date = action.date,
                        expense = action.expense,
                        categoryId = action.category?.id,
                        budgetId = action.budget.id!!,
                        createdBy = createdBy.id!!
                    )
                )
                dispatch(TransactionAction.SaveTransactionSuccess(transaction))
            }
            state().copy(loading = true)
        }

        is TransactionAction.SaveTransactionSuccess -> {
            val currentState = state()
            val transactions = currentState.transactions?.toMutableList() ?: mutableListOf()
            transactions.replace(action.transaction)
            transactions.sortByDescending { it.date }
            dispatch(TransactionAction.LoadTransactionsSuccess(transactions))
            currentState.copy(
                loading = false,
                transactions = transactions.toList(),
                selectedTransaction = action.transaction.id,
                selectedTransactionCreatedBy = currentState.user,
                route = Route.Transactions(action.transaction.id),
                editingTransaction = false
            )
        }

        is BudgetAction.BudgetSelected -> state().copy(transactions = null).also {
            launch {
                try {
                    val transactions = transactionRepository.findAll(
                        start = it.from,
                        end = it.to,
                        budgetIds = listOf(action.id)
                    )
                    dispatch(TransactionAction.LoadTransactionsSuccess(transactions))
                } catch (e: Exception) {
                    dispatch(TransactionAction.LoadTransactionsFailed(e))
                }
            }
        }

        is ConfigAction.Logout -> state().copy(
            transactions = null,
            selectedTransaction = null,
            editingTransaction = false
        )

        is TransactionAction.EditTransaction -> state().copy(
            editingTransaction = true,
            selectedTransaction = action.id
        )

        is TransactionAction.SelectTransaction -> {
            launch {
                val currentState = state()
                val transaction = currentState.transactions!!.first { it.id == action.id }
                val createdBy = userRepository.findById(transaction.createdBy)
                dispatch(TransactionAction.TransactionSelected(transaction, createdBy))
            }
            state().copy(
                loading = true,
                selectedTransaction = action.id,
                route = Route.Transactions(action.id)
            )
        }

        is TransactionAction.TransactionSelected -> state().copy(
            selectedTransaction = action.transaction.id,
            selectedTransactionCreatedBy = action.createdBy
        )

        is TransactionAction.DeleteTransaction -> state().copy(loading = true).also {
            launch {
                try {
                    transactionRepository.delete(action.id)
                    dispatch(TransactionAction.TransactionDeleted(action.id))
                } catch (e: Exception) {
                    e.printStackTrace()
                    dispatch(TransactionAction.TransactionDeletedFailure(action.id))
                }
            }
        }

        is TransactionAction.TransactionDeleted -> {
            val currentState = state()
            currentState.copy(
                transactions = currentState.transactions?.filter { it.id != action.id },
                selectedTransaction = if (currentState.selectedTransaction == action.id) null else currentState.selectedTransaction,
                editingTransaction = if (currentState.selectedTransaction == action.id) false else currentState.editingTransaction,
                route = if (currentState.selectedTransaction == action.id) Route.Transactions() else currentState.route,
            )
        }

        is TransactionAction.TransactionDeletedFailure -> state().also {
            emit(Effect.Error("Failed to delete transaction"))
        }

        else -> state()
    }
}

fun Instant.stripTime(): Instant {
    val localDateTime = toLocalDateTime(TimeZone.UTC)
    return minus(localDateTime.hour.hours)
        .minus(localDateTime.minute.minutes)
        .minus(localDateTime.second.seconds)
        .minus(localDateTime.nanosecond.nanoseconds)
}

fun List<Transaction>.groupByDate(): Map<String, List<Transaction>> {
    val groups = mutableMapOf<String, List<Transaction>>()
    forEach { transaction ->
        println("Groups: ${groups.size}")
        val key = transaction.date.stripTime().toString()
        val list = groups[key]?.toMutableList() ?: mutableListOf()
        list.add(transaction)
        list.sortByDescending { t -> t.date }
        groups[key] = list
    }
    return groups
}