package com.pichurchyk.budgetsaver.ui.screen.transaction.edit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.data.ext.toTransactionCreation
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import com.pichurchyk.budgetsaver.domain.repository.CurrencyRepository
import com.pichurchyk.budgetsaver.domain.usecase.DeleteTransactionUseCase
import com.pichurchyk.budgetsaver.domain.usecase.EditTransactionUseCase
import com.pichurchyk.budgetsaver.domain.usecase.LoadTransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Currency
import kotlin.text.iterator

class EditTransactionViewModel(
    private val transactionId: String,
    private val loadTransactionUseCase: LoadTransactionUseCase,
    private val editTransactionUseCase: EditTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _viewState: MutableStateFlow<EditTransactionViewState> = MutableStateFlow(
        EditTransactionViewState()
    )
    val viewState = _viewState.asStateFlow()

    init {
        loadTransaction()
        loadInitialCurrencies()
    }

    private fun loadInitialCurrencies() {
        currencyRepository.getAllCurrencies()
            .onEach { currencies ->
                _viewState.update { currentState ->
                    val defaultTransactionCurrency = currentState.transaction.currency

                    currentState.copy(
                        allCurrencies = currencies,
                        transaction = currentState.transaction.copy(currency = defaultTransactionCurrency)
                    )
                }
            }
            .catch { e ->
                _viewState.update {
                    it.copy(
                        allCurrencies = null
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadTransaction() {
        viewModelScope.launch {
            loadTransactionUseCase
                .invoke(transactionId)
                .onStart {
                    _viewState.update { currentState ->
                        currentState.copy(status = EditTransactionUiStatus.Loading)
                    }
                }
                .catch { e ->
                    _viewState.update { currentState ->
                        currentState.copy(
                            status = EditTransactionUiStatus.Error(
                                error = e as DomainException,
                                lastAction = { loadTransaction() })
                        )
                    }
                }
                .collect { transaction ->
                    _viewState.update { currentState ->
                        currentState.copy(
                            status = EditTransactionUiStatus.Idle,
                            transaction = transaction.toTransactionCreation()
                        )
                    }
                }
        }
    }

    fun handleIntent(intent: EditTransactionIntent) {
        when (intent) {
            is EditTransactionIntent.Submit -> submit()
            is EditTransactionIntent.Delete -> delete()
            is EditTransactionIntent.SubmitDelete -> submitDelete()
            is EditTransactionIntent.CancelDelete -> cancelDelete()
            is EditTransactionIntent.ChangeCurrency -> changeCurrency(intent.currency)
            is EditTransactionIntent.ChangeValue -> changeValue(intent.value)
            is EditTransactionIntent.ChangeType -> changeType(intent.value)
            is EditTransactionIntent.ChangeNotes -> changeNotes(intent.value)
            is EditTransactionIntent.ChangeTitle -> changeTitle(intent.value)
            is EditTransactionIntent.SearchCurrency -> changeSearchCurrencyValue(intent.value)
            is EditTransactionIntent.ChangeCategory -> changeCategory(intent.value)
            is EditTransactionIntent.DismissNotification -> dismissNotification()
        }
    }

    private fun dismissNotification() {
        _viewState.update {
            it.copy(
                status = EditTransactionUiStatus.Idle
            )
        }
    }

    private fun changeCategory(category: TransactionCategory?) {
        _viewState.update { currentViewState ->
            currentViewState.copy(
                transaction = currentViewState.transaction.copy(mainCategory = category),
            )
        }
    }

    private fun changeType(type: TransactionType) {
        _viewState.update { currentViewState ->
            currentViewState.copy(transaction = currentViewState.transaction.copy(type = type))
        }
    }

    private fun changeSearchCurrencyValue(value: String) {
        _viewState.update { currentViewState ->
            currentViewState.copy(currenciesSearch = value)
        }
    }

    private fun changeValue(value: String) {
        _viewState.update { currentViewState ->
            val filteredValue = filterAmountInput(value)
            currentViewState.copy(
                transaction = currentViewState.transaction.copy(value = filteredValue),
                validationError = currentViewState.validationError.filterNot { it == EditTransactionValidationError.EMPTY_AMOUNT }
            )
        }
    }

    private fun changeNotes(value: String) {
        _viewState.update { currentViewState ->
            currentViewState.copy(transaction = currentViewState.transaction.copy(notes = value))
        }
    }

    private fun changeTitle(value: String) {
        _viewState.update { currentViewState ->
            currentViewState.copy(
                transaction = currentViewState.transaction.copy(title = value),
            )
        }
    }

    private fun filterAmountInput(input: String): String {
        val filtered = buildString {
            var dotAdded = false
            var digitsAfterDot = 0

            for (char in input) {
                when {
                    char.isDigit() -> {
                        if (!dotAdded || digitsAfterDot < 2) {
                            append(char)
                            if (dotAdded) digitsAfterDot++
                        }
                    }

                    char == '.' && !dotAdded -> {
                        if (isNotEmpty()) {
                            append(char)
                            dotAdded = true
                        }
                    }
                }
            }
        }

        return filtered
    }

    private fun changeCurrency(currency: Currency) {
        _viewState.update { currentViewState ->
            currentViewState.copy(transaction = currentViewState.transaction.copy(currency = currency))
        }
    }

    private fun delete() {
        _viewState.update { currentViewState ->
            currentViewState.copy(status = EditTransactionUiStatus.Deleting)
        }
    }

    private fun submitDelete() {
        viewModelScope.launch {
            deleteTransactionUseCase.invoke(transactionId)
                .onStart {
                    _viewState.update { currentState ->
                        currentState.copy(status = EditTransactionUiStatus.Loading)
                    }
                }
                .catch { e ->
                    _viewState.update {
                        it.copy(
                            status = EditTransactionUiStatus.Error(
                                error = e as DomainException,
                                lastAction = { submit() }
                            )
                        )
                    }
                }
                .collect {
                    _viewState.update { it.copy(status = EditTransactionUiStatus.Success(action = EditTransactionAction.DELETE)) }
                }
        }
    }

    private fun cancelDelete() {
        _viewState.update { currentViewState ->
            currentViewState.copy(status = EditTransactionUiStatus.Idle)
        }
    }

    private fun submit() {
        _viewState.update {
            it.copy(
                status = EditTransactionUiStatus.Idle,
                validationError = emptyList()
            )
        }

        val currentData = _viewState.value
        val validationErrors =
            performValidation(currentData.transaction, currentData.transaction.type)

        if (validationErrors.isNotEmpty()) {
            _viewState.update {
                it.copy(
                    validationError = validationErrors,
                    status = EditTransactionUiStatus.ValidationError
                )
            }

            return
        }

        viewModelScope.launch {
            _viewState.value.transaction.let { transactionToSubmit ->
                editTransactionUseCase.invoke(transactionId, transactionToSubmit)
                    .onStart {
                        _viewState.update { it.copy(status = EditTransactionUiStatus.Loading) }
                    }
                    .catch { e ->
                        _viewState.update {
                            it.copy(
                                status = EditTransactionUiStatus.Error(
                                    error = e as DomainException,
                                    lastAction = { submit() }
                                )
                            )
                        }
                    }
                    .collect {
                        _viewState.update { it.copy(status = EditTransactionUiStatus.Success(action = EditTransactionAction.EDIT)) }
                    }
            }
        }
    }

    private fun performValidation(
        transaction: TransactionCreation,
        type: TransactionType
    ): List<EditTransactionValidationError> {
        val errors = mutableListOf<EditTransactionValidationError>()

        val parsedValue = transaction.value.toDoubleOrNull()
        if (parsedValue == null || parsedValue <= 0.0) {
            errors.add(EditTransactionValidationError.EMPTY_AMOUNT)
        }

        return errors
    }
}