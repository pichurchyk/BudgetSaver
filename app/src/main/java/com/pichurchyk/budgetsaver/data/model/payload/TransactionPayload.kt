package com.pichurchyk.budgetsaver.data.model.payload

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionPayload(
    val title: String,
    val value: Long,
    val currency: String,
    val notes: String,

    @SerialName("dateMillis")
    val dateMillis: Long,
    val dateTimeZone: String,

    @SerialName("main_category")
    val mainCategory: String
)