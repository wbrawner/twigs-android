package com.wbrawner.twigs.shared.transaction

import com.wbrawner.twigs.shared.Identifiable
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Transaction(
    override val id: String? = null,
    val title: String,
    @Serializable(with = DateSerializer::class)
    val date: Instant,
    val description: String? = null,
    val amount: Long,
    val categoryId: String? = null,
    val budgetId: String,
    val expense: Boolean,
    val createdBy: String
) : Identifiable

@Serializable
data class BalanceResponse(val balance: Long)

object DateSerializer : KSerializer<Instant> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: Instant) = encoder.encodeString(value.toString())
        override fun deserialize(decoder: Decoder): Instant = Instant.parse(decoder.decodeString())
}