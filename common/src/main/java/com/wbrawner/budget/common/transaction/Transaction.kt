package com.wbrawner.budget.common.transaction

import com.wbrawner.budget.common.Identifiable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.util.*

@Serializable
data class Transaction(
        override val id: String? = null,
        val title: String,
        @Serializable(with = DateSerializer::class)
        val date: Date,
        val description: String? = null,
        val amount: Long,
        val categoryId: String? = null,
        val budgetId: String,
        val expense: Boolean,
        val createdBy: String
): Identifiable

@Serializable
data class BalanceResponse(val balance: Long)

object DateSerializer : KSerializer<Date> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: Date) = encoder.encodeString(value.toInstant().toString())
        override fun deserialize(decoder: Decoder): Date = Date.from(Instant.parse(decoder.decodeString()))
}