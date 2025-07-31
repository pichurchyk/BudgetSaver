package com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory

sealed class ProfileCategoriesUiStatus {
    data object Idle : ProfileCategoriesUiStatus()
    data object Loading : ProfileCategoriesUiStatus()
    data class Error(
        val error: DomainException,
        val lastAction: () -> Unit
    ) : ProfileCategoriesUiStatus()
}

data class ProfileCategoriesViewState(
    val status: ProfileCategoriesUiStatus = ProfileCategoriesUiStatus.Idle,
    val categories: List<TransactionCategory> = emptyList(),
    val search: String = ""
) {
    val filteredCategories: List<TransactionCategory>
        get() = categories.filter {
            it.asPrettyText.lowercase().contains(search.lowercase())
        }
}