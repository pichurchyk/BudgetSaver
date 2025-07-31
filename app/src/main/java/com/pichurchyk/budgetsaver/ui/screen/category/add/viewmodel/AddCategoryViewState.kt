package com.pichurchyk.budgetsaver.ui.screen.category.add.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategoryCreation

data class AddCategoryViewState(
    val status: AddCategoryUiStatus = AddCategoryUiStatus.Idle,
    val model: TransactionCategoryCreation = TransactionCategoryCreation()
)

sealed interface AddCategoryUiStatus {
    data object Loading: AddCategoryUiStatus
    data object Idle: AddCategoryUiStatus
    data class Error(
        val error: DomainException,
        val lastAction: () -> Unit
    ): AddCategoryUiStatus
}