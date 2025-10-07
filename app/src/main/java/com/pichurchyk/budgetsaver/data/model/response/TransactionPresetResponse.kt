package com.pichurchyk.budgetsaver.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionPresetResponse(
    @SerialName("id")
    val uuid: String,

    val title: String?,
    val value: String,
    val currency: String,

    @SerialName("main_category")
    val mainCategory: MainCategoryResponse?,
    val notes: String? = null,
)