package com.pichurchyk.budgetsaver.ui.screen.transaction.add.category.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.domain.usecase.GetTransactionsCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.plus
import kotlin.collections.toList

class CategorySelectorViewModel(
    val isMultiSelect: Boolean,
    val isNullable: Boolean,
    val initialSelectedValues: List<TransactionCategory>,
    val getTransactionsCategoriesUseCase: GetTransactionsCategoriesUseCase
) : ViewModel() {

    private val _state =
        MutableStateFlow<CategorySelectorViewState>(CategorySelectorViewState.Loading)
    val state = _state.asStateFlow()

    fun handleIntent(intent: CategorySelectorIntent) {
        when (intent) {
            is CategorySelectorIntent.LoadCategories -> {
                loadCategories()
            }

            is CategorySelectorIntent.ToggleCategory -> {
                toggleCategory(intent.category)
            }

            is CategorySelectorIntent.ToggleAllCategories -> {
                toggleAllCategoriesFilter()
            }

            is CategorySelectorIntent.ChangeSearchValue -> {
                changeSearchValue(intent.value)
            }
        }
    }

    private fun changeSearchValue(value: String) {
        _state.update { currentState ->
            if (currentState !is CategorySelectorViewState.Loaded) return@update currentState

            currentState.copy(searchValue = value)
        }
    }

    private fun toggleCategory(clickedCategory: TransactionCategory) {
        _state.update { currentState ->
            if (currentState !is CategorySelectorViewState.Loaded) return@update currentState

            val currentSelected = currentState.selected.toSet()
            val isSelected = clickedCategory in currentSelected

            val newSelected = when {
                !isMultiSelect -> {
                    if (isSelected) {
                        if (isNullable) emptyList() else listOf(clickedCategory)
                    } else {
                        listOf(clickedCategory)
                    }
                }

                isSelected -> {
                    if (currentSelected.size == 1 && !isNullable) {
                        currentSelected
                    } else {
                        currentSelected - clickedCategory
                    }
                }

                else -> currentSelected + clickedCategory
            }

            currentState.copy(selected = newSelected.toList())
        }
    }

    private fun toggleAllCategoriesFilter() {
        if (!isMultiSelect) return

        _state.update { currentState ->
            if (currentState !is CategorySelectorViewState.Loaded) return@update currentState

            val allSelected = currentState.selected.containsAll(currentState.categories)

            currentState.copy(
                selected = if (allSelected) emptyList() else currentState.categories
            )
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getTransactionsCategoriesUseCase.invoke()
                .onStart {
                    _state.update { CategorySelectorViewState.Loading }
                }
                .catch { error ->
                    _state.update { CategorySelectorViewState.Error(error.message) }
                }
                .collect { categories ->
                    _state.update {
                        CategorySelectorViewState.Loaded(
                            categories = categories,
                            selected = initialSelectedValues
                        )
                    }
                }
        }
    }

}