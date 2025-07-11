package com.pichurchyk.budgetsaver.data.model.payload

import com.pichurchyk.budgetsaver.domain.model.serializer.BigIntegerSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.math.BigInteger

@Serializable
data class TransactionPayload(
    val title: String,

    @Serializable(with = BigIntegerSerializer::class)
    val value: BigInteger,
    val currency: String,
    val notes: String,

    @SerialName("dateMillis")
    val dateMillis: Long,
    val dateTimeZone: String,

    @SerialName("main_category")
    val mainCategory: String
)