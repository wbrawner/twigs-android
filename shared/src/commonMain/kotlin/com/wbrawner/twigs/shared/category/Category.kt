package com.wbrawner.twigs.shared.category

import com.wbrawner.twigs.shared.Identifiable
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val budgetId: String,
    override val id: String? = null,
    val title: String,
    val description: String? = null,
    val amount: Long,
    val expense: Boolean = true,
    val archived: Boolean = false
) : Identifiable