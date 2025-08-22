package com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.data.datasource.SessionManager
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val sessionManager: SessionManager,
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _state: MutableStateFlow<DashboardViewState> =
        MutableStateFlow(DashboardViewState(
            transactionsStatus = TransactionsUiStatus.Loading,
            currenciesStatus = CurrenciesUiStatus.Loading
        ))
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

            is DashboardIntent.DeleteTransaction -> {
                deleteTransaction(intent.transaction)
            }

            is DashboardIntent.Init -> {
                loadCurrencies()
            }
        }
    }

    private fun loadCurrencies() {
        _state.update { it.copy(currenciesStatus = CurrenciesUiStatus.Loading) }

        currencyRepository.getAllCurrencies()
            .onEach { currenciesFromRepo ->
                val currencyCodes = currenciesFromRepo.map { it.currencyCode }

                _state.update { currentState ->
                    currentState.copy(
                        currenciesStatus = CurrenciesUiStatus.Idle,
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
                    _state.update { it.copy(selectedCurrency = null) }
                    loadData()
                }
            }
            .catch { error ->
                _state.update {
                    it.copy(
                        currenciesStatus = CurrenciesUiStatus.Error(
                            error as? DomainException ?: DomainException.UnknownApiException()
                        ) { loadCurrencies() }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            _state.update { it.copy(transactionsStatus = TransactionsUiStatus.Loading) }
            deleteTransactionUseCase.invoke(transaction.uuid)
                .onStart { }
                .catch { error ->
                    _state.update {
                        it.copy(
                            transactionsStatus = TransactionsUiStatus.Error(
                                error as DomainException
                            ) { deleteTransaction(transaction) })
                    }
                }
                .collect {
                    _state.update { currentState ->
                        val updatedTransactions =
                            currentState.transactions?.map { transactionsByCurrency ->
                                if (transactionsByCurrency.currencyCode == currentState.selectedCurrency) {
                                    transactionsByCurrency.copy(
                                        transactions = transactionsByCurrency.transactions.filter { it.uuid != transaction.uuid }
                                    )
                                } else {
                                    transactionsByCurrency
                                }
                            }
                        currentState.copy(
                            transactionsStatus = TransactionsUiStatus.Idle,
                            transactions = updatedTransactions
                        )
                    }
                }
        }
    }

    private fun selectCurrency(currency: String) {
        _state.update { currentState ->
            currentState.copy(selectedCurrency = currency)
        }

        loadData()
    }

    private fun toggleAllTypesFilter() {
        _state.update { currentState ->
            val updatedTransactions = currentState.transactions?.map { transactionsByCurrency ->
                if (transactionsByCurrency.currencyCode == currentState.selectedCurrency) {
                    transactionsByCurrency.copy(selectedTransactionType = TransactionType.entries)
                } else {
                    transactionsByCurrency
                }
            }
            currentState.copy(transactions = updatedTransactions)
        }
    }

    private fun toggleTypeFilter(clickedFilter: TransactionType) {
        _state.update { currentState ->
            val updatedTransactions = currentState.transactions?.map { transactionsByCurrency ->
                if (transactionsByCurrency.currencyCode == currentState.selectedCurrency) {
                    val currentFilters = transactionsByCurrency.selectedTransactionType.toSet()
                    val newFilters = when {
                        currentFilters.size == 1 && currentFilters.contains(clickedFilter) -> TransactionType.entries
                        clickedFilter in currentFilters && currentFilters.size > 1 -> currentFilters - clickedFilter
                        clickedFilter !in currentFilters -> currentFilters + clickedFilter
                        else -> currentFilters
                    }
                    transactionsByCurrency.copy(selectedTransactionType = newFilters.toList())
                } else {
                    transactionsByCurrency
                }
            }
            currentState.copy(transactions = updatedTransactions)
        }
    }

    private fun toggleCategoriesFilter(clickedCategory: TransactionCategory?) {
        _state.update { currentState ->
            val updatedTransactions = currentState.transactions?.map { transactionsByCurrency ->
                if (transactionsByCurrency.currencyCode == currentState.selectedCurrency) {
                    val currentFilters = transactionsByCurrency.selectedCategories.toSet()
                    val newFilters = when {
                        currentFilters.size == 1 && currentFilters.contains(clickedCategory) -> transactionsByCurrency.allCategories
                        clickedCategory in currentFilters && currentFilters.size > 1 -> currentFilters - clickedCategory
                        clickedCategory !in currentFilters -> currentFilters + clickedCategory
                        else -> currentFilters
                    }
                    transactionsByCurrency.copy(selectedCategories = newFilters.toList())
                } else {
                    transactionsByCurrency
                }
            }
            currentState.copy(transactions = updatedTransactions)
        }
    }

    private fun toggleAllCategoriesFilter() {
        _state.update { currentState ->
            val updatedTransactions = currentState.transactions?.map { transactionsByCurrency ->
                if (transactionsByCurrency.currencyCode == currentState.selectedCurrency) {
                    transactionsByCurrency.copy(selectedCategories = transactionsByCurrency.allCategories)
                } else {
                    transactionsByCurrency
                }
            }
            currentState.copy(transactions = updatedTransactions)
        }
    }

    private fun loadData() {
        state.value.selectedCurrency?.let { selectedCurrency ->
            viewModelScope.launch {
                getTransactionsUseCase.invoke(selectedCurrency)
                    .onStart {
                        _state.update { it.copy(transactionsStatus = TransactionsUiStatus.Loading) }
                    }
                    .catch { error ->
                        _state.update {
                            it.copy(
                                transactionsStatus = TransactionsUiStatus.Error(
                                    error as DomainException
                                ) { loadData() })
                        }
                    }
                    .collect { data ->
                        val allCategories = data.map { tx -> tx.mainCategory }.distinct()
                        val allTransactionTypes = TransactionType.entries

                        val transactionsByCurrency = TransactionsByCurrency(
                            transactions = data,
                            currencyCode = selectedCurrency,
                            selectedCategories = allCategories,
                            selectedTransactionType = allTransactionTypes
                        )

                        val existingTransactions = state.value.transactions ?: emptyList()

                        val existingIndex =
                            existingTransactions.indexOfFirst { it.currencyCode == selectedCurrency }

                        val updatedTransactions = if (existingIndex != -1) {
                            existingTransactions.toMutableList().apply {
                                this[existingIndex] = transactionsByCurrency
                            }
                        } else {
                            existingTransactions + transactionsByCurrency
                        }

                        _state.update {
                            it.copy(
                                transactionsStatus = TransactionsUiStatus.Idle,
                                transactions = updatedTransactions
                            )
                        }
                    }
            }
        }
    }
}