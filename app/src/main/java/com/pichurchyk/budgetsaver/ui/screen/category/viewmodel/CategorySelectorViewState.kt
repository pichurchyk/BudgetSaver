package com.pichurchyk.budgetsaver.ui.screen.category.viewmodel

import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory

sealed class CategorySelectorViewState {

    data object Loading : CategorySelectorViewState()

    data class Loaded(
        val categories: List<TransactionCategory>,
        val selected: List<TransactionCategory>,
        val searchValue: String = "",
    ) : CategorySelectorViewState() {

        val filteredBySearchCategories: List<TransactionCategory>
            get() = categories.filter { it.asPrettyText.lowercase().contains(searchValue.lowercase()) }

        val unselected: List<TransactionCategory>
            get() = filteredBySearchCategories.filterNot { selected.contains(it) }
    }

    data class Error(val message: String?) : CategorySelectorViewState()

}