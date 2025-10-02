package com.pichurchyk.budgetsaver.ui.screen.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionDate
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import com.pichurchyk.budgetsaver.domain.repository.CurrencyRepository
import com.pichurchyk.budgetsaver.domain.usecase.transaction.DeleteTransactionUseCase
import com.pichurchyk.budgetsaver.domain.usecase.transaction.GetTransactionsUseCase
import com.pichurchyk.budgetsaver.ui.ext.DateUtils.asEndOfTheDay
import com.pichurchyk.budgetsaver.ui.ext.DateUtils.asStartOfTheDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.util.Currency

class DashboardViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private var allTransactions: List<Transaction> = emptyList()

    private val _state: MutableStateFlow<DashboardViewState> =
        MutableStateFlow(
            DashboardViewState(
                status = DashboardUiStatus.LoadingAll,
            )
        )

    val state = _state
        .map { viewState ->
            val filteredTransactions = filterTransactions(
                allTransactions,
                viewState.selectedCategories,
                viewState.selectedTransactionType,
                viewState.datePeriod
            )

            val totalIncomes = calculateTotalIncomes(filteredTransactions, viewState.selectedCurrency)
            val totalExpenses = calculateTotalExpenses(filteredTransactions, viewState.selectedCurrency)

            viewState.copy(
                filteredTransactions = filteredTransactions,
                totalIncomes = totalIncomes,
                totalExpenses = totalExpenses
            )
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardViewState(status = DashboardUiStatus.LoadingAll)
        )

    init {
        loadCurrencies()
    }

    fun handleIntent(intent: DashboardIntent) {
        when (intent) {
            is DashboardIntent.LoadData -> loadData()
            is DashboardIntent.ToggleTypeFilter -> toggleTypeFilter(intent.type)
            is DashboardIntent.ToggleAllTypesFilter -> toggleAllTypesFilter()
            is DashboardIntent.ToggleAllCategoriesFilter -> toggleAllCategoriesFilter()
            is DashboardIntent.ToggleCategoriesFilter -> toggleCategoriesFilter(intent.category)
            is DashboardIntent.SelectCurrency -> selectCurrency(intent.currency)
            is DashboardIntent.DeleteTransaction -> deleteTransaction(intent.transaction)
            is DashboardIntent.ChangeDateRange -> changeDateRange(intent.dateRange)
            is DashboardIntent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        loadData()
    }

    private suspend fun filterTransactions(
        allTransactions: List<Transaction>,
        selectedCategories: List<TransactionCategory?>,
        selectedTransactionType: List<TransactionType>,
        datePeriod: Pair<TransactionDate?, TransactionDate?>
    ): List<Transaction> {
        return allTransactions
            .filter { it.mainCategory in selectedCategories }
            .filter { tx ->
                when {
                    selectedTransactionType.containsAll(TransactionType.entries) -> true
                    tx.value.amountMinor >= BigInteger("0") -> TransactionType.INCOMES in selectedTransactionType
                    else -> TransactionType.EXPENSES in selectedTransactionType
                }
            }
            .filter { tx ->
                val (from, to) = datePeriod
                when {
                    from == null && to == null -> true // no filter
                    from != null && to == null -> tx.date.dateInstant >= from.dateInstant
                    from == null && to != null -> tx.date.dateInstant <= to.dateInstant
                    else -> tx.date.dateInstant in from!!.dateInstant..to!!.dateInstant
                }
            }
    }

    private suspend fun calculateTotalIncomes(
        filteredTransactions: List<Transaction>,
        selectedCurrency: Currency?
    ): Money {
        return Money(
            filteredTransactions.filter { it.value.amountMinor > BigInteger("0") }
                .sumOf { it.value.amountMinor },
            selectedCurrency?.currencyCode ?: ""
        )
    }

    private suspend fun calculateTotalExpenses(
        filteredTransactions: List<Transaction>,
        selectedCurrency: Currency?
    ): Money {
        return Money(
            filteredTransactions.filter { it.value.amountMinor < BigInteger("0") }
                .sumOf { it.value.amountMinor },
            selectedCurrency?.currencyCode ?: ""
        )
    }

    private fun changeDateRange(
        dateRange: Pair<TransactionDate?, TransactionDate?>
    ) {
        _state.update {
            it.copy(datePeriod = dateRange)
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
                .collect { currencies ->
                    _state.update { currentState ->
                        currentState.copy(availableCurrencies = currencies)
                    }

                    if (currencies.isNotEmpty() && state.value.selectedCurrency == null) {
                        selectCurrency(currencies.first())
                    } else {
                        allTransactions = emptyList()
                    }
                }
        }
    }

    private fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            deleteTransactionUseCase.invoke(transaction.uuid)
                .onStart {
                    _state.update {
                        it.copy(status = DashboardUiStatus.IdleDeletingTransaction(transaction))
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

    private fun selectCurrency(currency: Currency) {
        _state.update { it.copy(selectedCurrency = currency) }
        loadData()
    }

    private fun toggleAllTypesFilter() {
        _state.update {
            it.copy(selectedTransactionType = TransactionType.entries)
        }
    }

    private fun toggleTypeFilter(clickedFilter: TransactionType) {
        _state.update { currentState ->
            val currentFilters = currentState.selectedTransactionType.toSet()
            val newFilters = when {
                currentFilters.size == 1 && currentFilters.contains(clickedFilter) -> TransactionType.entries
                clickedFilter in currentFilters && currentFilters.size > 1 -> currentFilters - clickedFilter
                clickedFilter !in currentFilters -> currentFilters + clickedFilter
                else -> currentFilters
            }
            currentState.copy(selectedTransactionType = newFilters.toList())
        }
    }

    private fun toggleCategoriesFilter(clickedCategory: TransactionCategory?) {
        _state.update { currentState ->
            val currentFilters = currentState.selectedCategories.toSet()
            val allCategories = currentState.allCategories.toSet()
            val newFilters = when {
                currentFilters.size == 1 && currentFilters.contains(clickedCategory) -> allCategories
                clickedCategory in currentFilters && currentFilters.size > 1 -> currentFilters - clickedCategory
                clickedCategory !in currentFilters -> currentFilters + clickedCategory
                else -> currentFilters
            }
            currentState.copy(selectedCategories = newFilters.toList())
        }
    }

    private fun toggleAllCategoriesFilter() {
        _state.update {
            it.copy(selectedCategories = it.allCategories)
        }
    }

    private fun loadData() {
        _state.value.selectedCurrency?.let { selectedCurrency ->
            viewModelScope.launch {
                getTransactionsUseCase.invoke(selectedCurrency.currencyCode)
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
                        allTransactions = data

                        val categories = data.map { it.mainCategory }.distinct()

                        val oldestTransactionDate =
                            data.minByOrNull { it.date.dateInstant.toEpochMilliseconds() }?.date?.asStartOfTheDay()

                        val newestTransactionDate =
                            data.maxByOrNull { it.date.dateInstant.toEpochMilliseconds() }?.date?.asEndOfTheDay()

                        _state.update {
                            it.copy(
                                status = DashboardUiStatus.Idle,
                                allCategories = categories,
                                selectedCategories = categories,
                                selectedTransactionType = TransactionType.entries,
                                datePeriod = oldestTransactionDate to newestTransactionDate
                            )
                        }
                    }
            }
        }
    }
}