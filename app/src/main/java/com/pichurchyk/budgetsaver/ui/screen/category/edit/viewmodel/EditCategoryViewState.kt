package com.pichurchyk.budgetsaver.ui.screen.category.edit.viewmodel

import com.pichurchyk.budgetsaver.domain.model.Emoji
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategoryCreation

data class EditCategoryViewState(
    val status: EditCategoryUiStatus = EditCategoryUiStatus.Idle,
    val model: TransactionCategoryCreation = TransactionCategoryCreation(),
    val searchEmojisValue: String = "",
    val availableEmojis: List<Emoji> = emptyList()
)

sealed interface EditCategoryUiStatus {
    data object Loading : EditCategoryUiStatus
    data object Idle : EditCategoryUiStatus
}
