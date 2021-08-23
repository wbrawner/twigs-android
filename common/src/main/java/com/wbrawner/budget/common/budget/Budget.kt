package com.wbrawner.budget.common.budget

import com.wbrawner.budget.common.Identifiable
import com.wbrawner.budget.common.user.UserPermission
import kotlinx.serialization.Serializable

@Serializable
data class Budget(
        override val id: String? = null,
        val name: String,
        val description: String? = null,
        val users: List<UserPermission> = emptyList()
) : Identifiable {
    override fun toString(): String {
        return name
    }
}
