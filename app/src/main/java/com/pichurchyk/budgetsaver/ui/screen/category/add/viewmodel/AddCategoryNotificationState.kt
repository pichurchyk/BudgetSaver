package com.pichurchyk.budgetsaver.ui.screen.category.add.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException

sealed class AddCategoryNotification {
    data class Error(val error: DomainException, val lastAction: (() -> Unit)?) : AddCategoryNotification()
    data object Success : AddCategoryNotification()
}

