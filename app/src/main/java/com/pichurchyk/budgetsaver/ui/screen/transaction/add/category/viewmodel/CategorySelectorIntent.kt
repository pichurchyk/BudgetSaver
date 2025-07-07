package com.pichurchyk.budgetsaver.ui.screen.transaction.add.category.viewmodel

import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory

sealed class CategorySelectorIntent {

    data object LoadCategories : CategorySelectorIntent()

    data class ToggleCategory(val category: TransactionCategory) : CategorySelectorIntent()

    data class ChangeSearchValue(val value: String) : CategorySelectorIntent()

    data object ToggleAllCategories : CategorySelectorIntent()
}