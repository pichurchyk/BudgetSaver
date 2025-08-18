package com.pichurchyk.budgetsaver.ui.screen.category.add.viewmodel

import com.pichurchyk.budgetsaver.domain.model.Emoji
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategoryCreation

data class AddCategoryViewState(
    val status: AddCategoryUiStatus = AddCategoryUiStatus.Idle,
    val model: TransactionCategoryCreation = TransactionCategoryCreation(),
    val searchEmojisValue: String = "",
    val availableEmojis: List<Emoji> = emptyList()
)

sealed interface AddCategoryUiStatus {
    data object Loading : AddCategoryUiStatus
    data object Idle : AddCategoryUiStatus
}