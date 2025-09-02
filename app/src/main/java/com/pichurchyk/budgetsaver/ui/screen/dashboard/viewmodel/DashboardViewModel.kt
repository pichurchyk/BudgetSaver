package com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionsByCurrency
import com.pichurchyk.budgetsaver.domain.repository.CurrencyRepository
import com.pichurchyk.budgetsaver.domain.usecase.DeleteTransactionUseCase
import com.pichurchyk.budgetsaver.domain.usecase.GetTransactionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _state: MutableStateFlow<DashboardViewState> =
        MutableStateFlow(
            DashboardViewState(
                status = DashboardUiStatus.LoadingAll,
            )
        )
    val state = _state.asStateFlow()

    fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadData -> loadData()
            is DashboardIntent.ToggleTypeFilter -> toggleTypeFilter(intent.type)
            is DashboardIntent.ToggleAllTypesFilter -> toggleAllTypesFilter()
            is DashboardIntent.ToggleAllCategoriesFilter -> toggleAllCategoriesFilter()
            is DashboardIntent.ToggleCategoriesFilter -> toggleCategoriesFilter(intent.category)
            is DashboardIntent.SelectCurrency -> selectCurrency(intent.currency)
            is DashboardIntent.DeleteTransaction -> deleteTransaction(intent.transaction)
            is DashboardIntent.Init -> loadCurrencies()
        }
    }

    private fun loadCurrencies() {
        viewModelScope.launch {

            currencyRepository.getAllCurrencies()
                .onStart {
                    _state.update { it.copy(status = DashboardUiStatus.LoadingAll) }
                }
                .catch { error ->
                    _state.update {
                        it.copy(
                            status = DashboardUiStatus.Error(
                                error as? DomainException ?: DomainException.UnknownApiException()
                            ) { loadCurrencies() }
                        )
                    }
                }
                .collect { currenciesFromRepo ->
                    val currencyCodes = currenciesFromRepo.map { it.currencyCode }

                    _state.update { currentState ->
                        currentState.copy(
                            availableCurrencies = currencyCodes
                        )
                    }

                    if (currencyCodes.isNotEmpty()) {
                        val currentSelected = state.value.selectedCurrency
                        if (currentSelected == null || !currencyCodes.contains(currentSelected)) {
                            selectCurrency(currencyCodes.first())
                        } else {
                            loadData()
                        }
                    } else {
                        _state.update {
                            it.copy(
                                selectedCurrency = null,
                                currentTransactions = null
                            )
                        }
                    }
                }
        }
    }

    private fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            deleteTransactionUseCase.invoke(transaction.uuid)
                .onStart {
                    _state.update {
                        it.copy(
                            status = DashboardUiStatus.IdleDeletingTransaction(transaction)
                        )
                    }
                }
                .catch { error ->
                    _state.update {
                        it.copy(
                            status = DashboardUiStatus.Error(
                                error as DomainException
                            ) { deleteTransaction(transaction) }
                        )
                    }
                }
                .collect {
                    loadData()
                }
        }
    }

    private fun selectCurrency(currency: String) {
        _state.update { it.copy(selectedCurrency = currency) }
        loadData()
    }

    private fun toggleAllTypesFilter() {
        _state.update { currentState ->
            currentState.currentTransactions?.let { current ->
                val updated = TransactionsByCurrency.create(
                    transactions = current.transactions,
                    currencyCode = current.currencyCode,
                    selectedCategories = current.selectedCategories,
                    selectedTransactionType = TransactionType.entries
                )
                currentState.copy(currentTransactions = updated)
            } ?: currentState
        }
    }

    private fun toggleTypeFilter(clickedFilter: TransactionType) {
        _state.update { currentState ->
            currentState.currentTransactions?.let { current ->
                val currentFilters = current.selectedTransactionType.toSet()
                val newFilters = when {
                    currentFilters.size == 1 && currentFilters.contains(clickedFilter) -> TransactionType.entries
                    clickedFilter in currentFilters && currentFilters.size > 1 -> currentFilters - clickedFilter
                    clickedFilter !in currentFilters -> currentFilters + clickedFilter
                    else -> currentFilters
                }

                val updated = TransactionsByCurrency.create(
                    transactions = current.transactions,
                    currencyCode = current.currencyCode,
                    selectedCategories = current.selectedCategories,
                    selectedTransactionType = newFilters.toList()
                )
                currentState.copy(currentTransactions = updated)
            } ?: currentState
        }
    }

    private fun toggleCategoriesFilter(clickedCategory: TransactionCategory?) {
        _state.update { currentState ->
            currentState.currentTransactions?.let { current ->
                val currentFilters = current.selectedCategories.toSet()
                val newFilters = when {
                    currentFilters.size == 1 && currentFilters.contains(clickedCategory) -> current.allCategories
                    clickedCategory in currentFilters && currentFilters.size > 1 -> currentFilters - clickedCategory
                    clickedCategory !in currentFilters -> currentFilters + clickedCategory
                    else -> currentFilters
                }

                val updated = TransactionsByCurrency.create(
                    transactions = current.transactions,
                    currencyCode = current.currencyCode,
                    selectedCategories = newFilters.toList(),
                    selectedTransactionType = current.selectedTransactionType
                )
                currentState.copy(currentTransactions = updated)
            } ?: currentState
        }
    }

    private fun toggleAllCategoriesFilter() {
        _state.update { currentState ->
            currentState.currentTransactions?.let { current ->
                val updated = TransactionsByCurrency.create(
                    transactions = current.transactions,
                    currencyCode = current.currencyCode,
                    selectedCategories = current.allCategories,
                    selectedTransactionType = current.selectedTransactionType
                )
                currentState.copy(currentTransactions = updated)
            } ?: currentState
        }
    }

    private fun loadData() {
        state.value.selectedCurrency?.let { selectedCurrency ->
            viewModelScope.launch {
                getTransactionsUseCase.invoke(selectedCurrency)
                    .onStart {
                        _state.update { it.copy(status = DashboardUiStatus.LoadingTransactions) }
                    }
                    .catch { error ->
                        _state.update {
                            it.copy(
                                status = DashboardUiStatus.Error(
                                    error as DomainException
                                ) { loadData() }
                            )
                        }
                    }
                    .collect { data ->
                        val currentTransactions = TransactionsByCurrency.create(
                            transactions = data,
                            currencyCode = selectedCurrency
                        )

                        _state.update {
                            it.copy(
                                status = DashboardUiStatus.Idle,
                                currentTransactions = currentTransactions
                            )
                        }
                    }
            }
        }
    }
}