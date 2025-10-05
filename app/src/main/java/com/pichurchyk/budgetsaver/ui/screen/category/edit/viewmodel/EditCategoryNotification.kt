package com.pichurchyk.budgetsaver.ui.screen.category.edit.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException

sealed class EditCategoryNotification {
    data class Error(val error: DomainException, val lastAction: (() -> Unit)?) : EditCategoryNotification()
    data object Success : EditCategoryNotification()
}
