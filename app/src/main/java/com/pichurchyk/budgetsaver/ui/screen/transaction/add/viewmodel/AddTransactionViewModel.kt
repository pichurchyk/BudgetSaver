package com.pichurchyk.budgetsaver.ui.screen.transaction.add.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import com.pichurchyk.budgetsaver.domain.usecase.AddTransactionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Currency
import kotlin.text.iterator

class AddTransactionViewModel(
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel() {

    private val _viewState: MutableStateFlow<AddTransactionViewState> = MutableStateFlow(
        AddTransactionViewState()
    )
    val viewState = _viewState.asStateFlow()

    fun handleIntent(intent: AddTransactionIntent) {
        when (intent) {
            is AddTransactionIntent.Submit -> submit()
            is AddTransactionIntent.ChangeCurrency -> changeCurrency(intent.currency)
            is AddTransactionIntent.ChangeValue -> changeValue(intent.value)
            is AddTransactionIntent.ChangeType -> changeType(intent.value)
            is AddTransactionIntent.ChangeNotes -> changeNotes(intent.value)
            is AddTransactionIntent.ChangeTitle -> changeTitle(intent.value)
            is AddTransactionIntent.SearchCurrency -> changeSearchCurrencyValue(intent.value)
            is AddTransactionIntent.ChangeCategory -> changeCategory(intent.value)
            is AddTransactionIntent.ClearData -> clearData()
            is AddTransactionIntent.DismissNotification -> dismissNotification()
        }
    }

    private fun dismissNotification() {
        _viewState.update {
            it.copy(
                status = AddTransactionUiStatus.Idle
            )
        }
    }

    private fun clearData() {
        _viewState.update { AddTransactionViewState() }
    }

    private fun changeCategory(category: TransactionCategory?) {
        _viewState.update { currentViewState ->
            currentViewState.copy(
                transaction = currentViewState.transaction.copy(mainCategory = category),
                validationError = currentViewState.validationError.filterNot { it == AddTransactionValidationError.EMPTY_CATEGORY }
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
                validationError = currentViewState.validationError.filterNot { it == AddTransactionValidationError.EMPTY_AMOUNT }
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
                validationError = currentViewState.validationError.filterNot { it == AddTransactionValidationError.EMPTY_TITLE }
            )
        }
    }

    private fun filterAmountInput(input: String): String {
        val filtered = buildString {
            var dotAdded = false
            for (char in input) {
                if (char.isDigit()) {
                    append(char)
                } else if (char == '.' && !dotAdded) {
                    if (isNotEmpty()) {
                        append(char)
                        dotAdded = true
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

    private fun submit() {
        _viewState.update { it.copy(status = AddTransactionUiStatus.Idle, validationError = emptyList()) }

        val currentData = _viewState.value
        val validationErrors = performValidation(currentData.transaction, currentData.transaction.type)

        if (validationErrors.isNotEmpty()) {
            _viewState.update {
                it.copy(
                    validationError = validationErrors,
                    status = AddTransactionUiStatus.ValidationError
                )
            }

            return
        }

        viewModelScope.launch {
            _viewState.value.transaction.let { transactionToSubmit ->
                addTransactionUseCase.invoke(transactionToSubmit)
                    .onStart {
                        _viewState.update { it.copy(status = AddTransactionUiStatus.Loading) }
                    }
                    .catch { e ->
                        _viewState.update {
                            it.copy(
                                status = AddTransactionUiStatus.Error(
                                    error = e as DomainException,
                                    lastAction = { submit() }
                                )
                            )
                        }
                    }
                    .onCompletion { cause ->
                        clearData()
                    }
                    .collect {
                        _viewState.update { it.copy(status = AddTransactionUiStatus.Success) }
                    }
            }
        }
    }

    private fun performValidation(
        transaction: TransactionCreation,
        type: TransactionType
    ): List<AddTransactionValidationError> {
        val errors = mutableListOf<AddTransactionValidationError>()

        val parsedValue = transaction.value.toDoubleOrNull()
        if (parsedValue == null || parsedValue <= 0.0) {
            errors.add(AddTransactionValidationError.EMPTY_AMOUNT)
        }

        if (transaction.title.isBlank()) {
            errors.add(AddTransactionValidationError.EMPTY_TITLE)
        }

        if (transaction.mainCategory == null) {
            errors.add(AddTransactionValidationError.EMPTY_CATEGORY)
        }

        return errors
    }
}