package com.pichurchyk.budgetsaver.domain.model.category

import androidx.compose.ui.graphics.Color

data class TransactionCategoryCreation(
    val title: String = "",
    val emoji: String = "",
    val color: Color? = null,
)