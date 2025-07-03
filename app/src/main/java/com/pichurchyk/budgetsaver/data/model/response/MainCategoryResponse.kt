package com.pichurchyk.budgetsaver.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MainCategoryResponse(

    @SerialName("id")
    val uuid: String,

    val emoji: String,
    val title: String,
    val color: String? = null,
)