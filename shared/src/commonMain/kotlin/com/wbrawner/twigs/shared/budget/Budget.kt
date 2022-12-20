package com.wbrawner.twigs.shared.budget

import com.wbrawner.twigs.shared.user.UserPermission
import kotlinx.serialization.Serializable

@Serializable
data class Budget(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val users: List<UserPermission> = emptyList()
)

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
