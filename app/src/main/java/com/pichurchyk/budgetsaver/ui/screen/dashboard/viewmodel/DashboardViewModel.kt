package com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import com.pichurchyk.budgetsaver.domain.usecase.GetTransactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Currency

class DashboardViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<DashboardViewState> =
        MutableStateFlow(DashboardViewState.Init)
    val state = _state.asStateFlow()

    fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadData -> {
                loadData()
            }

            is DashboardIntent.ToggleTypeFilter -> {
                toggleTypeFilter(intent.type)
            }

            is DashboardIntent.ToggleAllTypesFilter -> {
                toggleAllTypesFilter()
            }

            is DashboardIntent.ToggleAllCategoriesFilter -> {
                toggleAllCategoriesFilter()
            }

            is DashboardIntent.ToggleCategoriesFilter -> {
                toggleCategoriesFilter(intent.category)
            }

            is DashboardIntent.SelectCurrency -> {
                selectCurrency(intent.currency)
            }
        }
    }

    private fun selectCurrency(currency: String) {
        _state.update { currentState ->
            if (currentState !is DashboardViewState.Loaded) return@update currentState

            currentState.copy(selectedCurrency = currency)
        }
    }

    private fun toggleAllTypesFilter() {
        _state.update { currentState ->
            if (currentState !is DashboardViewState.Loaded) return@update currentState

            val updatedAllTransactions = currentState.allTransactions.map {
                if (it.transactions.currencyCode == currentState.selectedCurrency) {
                    it.copy(selectedTransactionType = TransactionType.entries)
                } else it
            }

            currentState.copy(allTransactions = updatedAllTransactions)
        }
    }

    private fun toggleTypeFilter(clickedFilter: TransactionType) {
        _state.update { currentState ->
            if (currentState !is DashboardViewState.Loaded) return@update currentState

            val updatedAllTransactions = currentState.allTransactions.map {
                if (it.transactions.currencyCode == currentState.selectedCurrency) {
                    val currentFilters = it.selectedTransactionType.toSet()

                    val newFilters = when {
                        currentFilters.size == 1 && currentFilters.contains(clickedFilter) ->
                            TransactionType.entries

                        clickedFilter in currentFilters && currentFilters.size > 1 ->
                            currentFilters - clickedFilter

                        clickedFilter !in currentFilters ->
                            currentFilters + clickedFilter

                        else -> currentFilters
                    }

                    it.copy(selectedTransactionType = newFilters.toList())
                } else it
            }

            currentState.copy(allTransactions = updatedAllTransactions)
        }
    }

    private fun toggleCategoriesFilter(clickedCategory: TransactionCategory) {
        _state.update { currentState ->
            if (currentState !is DashboardViewState.Loaded) return@update currentState

            val updatedAllTransactions = currentState.allTransactions.map {
                if (it.transactions.currencyCode == currentState.selectedCurrency) {
                    val currentFilters = it.selectedCategories.toSet()

                    val newFilters = when {
                        currentFilters.size == 1 && currentFilters.contains(clickedCategory) ->
                            it.transactions.allCategories

                        clickedCategory in currentFilters && currentFilters.size > 1 ->
                            currentFilters - clickedCategory

                        clickedCategory !in currentFilters ->
                            currentFilters + clickedCategory

                        else -> currentFilters
                    }

                    it.copy(selectedCategories = newFilters.toList())
                } else it
            }

            currentState.copy(allTransactions = updatedAllTransactions)
        }
    }

    private fun toggleAllCategoriesFilter() {
        _state.update { currentState ->
            if (currentState !is DashboardViewState.Loaded) return@update currentState

            val updatedAllTransactions = currentState.allTransactions.map {
                if (it.transactions.currencyCode == currentState.selectedCurrency) {
                    it.copy(selectedCategories = it.transactions.allCategories)
                } else it
            }

            currentState.copy(allTransactions = updatedAllTransactions)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            getTransactionsUseCase.invoke()
                .onStart {
                    _state.update { DashboardViewState.Loading }
                }
                .catch { error ->
                    _state.update { DashboardViewState.Error(error.message) }
                }
                .collect { data ->
                    val items = data.map {
                        val selectedCategories = it.transactions.map { it.mainCategory }.distinct()
                        val selectedTransactionType = TransactionType.entries

                        TransactionsWithFilters(
                            it,
                            selectedCategories,
                            selectedTransactionType
                        )
                    }

                    _state.update {
                        DashboardViewState.Loaded(items)
                    }
                }
        }
    }

}