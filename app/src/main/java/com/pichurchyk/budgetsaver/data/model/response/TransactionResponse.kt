package com.pichurchyk.budgetsaver.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionResponse(
    @SerialName("id")
    val uuid: String,

    val title: String,
    val value: String,
    val currency: String,
    val notes: String,

    @SerialName("dateMillis")
    val dateMillis: Long,
    val dateTimeZone: String,

    @SerialName("main_category")
    val mainCategory: MainCategoryResponse,

    @SerialName("sub_category")
    val subCategory: List<String>? = null
)