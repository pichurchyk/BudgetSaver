package com.pichurchyk.budgetsaver.ui.screen.preset.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.preset.TransactionPresetCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import com.pichurchyk.budgetsaver.domain.repository.CurrencyRepository
import com.pichurchyk.budgetsaver.domain.usecase.preset.AddPresetUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Currency

class AddPresetViewModel(
    private val currencyRepository: CurrencyRepository,
    private val addPresetUseCase: AddPresetUseCase,
) : ViewModel() {

    private val _viewState: MutableStateFlow<AddPresetViewState> = MutableStateFlow(
        AddPresetViewState()
    )
    val viewState = _viewState.asStateFlow()

    init {
        loadInitialCurrencies()
    }

    fun handleIntent(intent: AddPresetIntent) {
        when (intent) {
            is AddPresetIntent.Submit -> submit()
            is AddPresetIntent.ChangeCurrency -> changeCurrency(intent.currency)
            is AddPresetIntent.ChangeValue -> changeValue(intent.value)
            is AddPresetIntent.ChangeType -> changeType(intent.value)
            is AddPresetIntent.ChangeNotes -> changeNotes(intent.value)
            is AddPresetIntent.ChangeTitle -> changeTitle(intent.value)
            is AddPresetIntent.SearchCurrency -> changeSearchCurrencyValue(intent.value)
            is AddPresetIntent.ChangeCategory -> changeCategory(intent.value)
            is AddPresetIntent.ClearData -> clearData()
            is AddPresetIntent.DismissNotification -> dismissNotification()
        }
    }

    private fun loadInitialCurrencies() {
        currencyRepository.getAllCurrencies().onEach { currencies ->
            _viewState.update { currentState ->
                val defaultTransactionCurrency = currentState.preset.currency

                currentState.copy(
                    allCurrencies = currencies,
                    preset = currentState.preset.copy(currency = defaultTransactionCurrency)
                )
            }
        }.catch { e ->
            _viewState.update {
                it.copy(
                    allCurrencies = null
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun dismissNotification() {
        _viewState.update {
            it.copy(
                status = AddPresetUiStatus.Idle
            )
        }
    }

    private fun clearData() {
        _viewState.update { currentState ->
            currentState.copy(
                preset = TransactionPresetCreation(
                    currency = currentState.preset.currency,
                    type = currentState.preset.type
                ),
                saveAsPreset = false,
            )
        }
    }

    private fun changeCategory(category: TransactionCategory?) {
        _viewState.update { currentViewState ->
            currentViewState.copy(
                preset = currentViewState.preset.copy(mainCategory = category),
            )
        }
    }

    private fun changeType(type: TransactionType) {
        _viewState.update { currentViewState ->
            currentViewState.copy(preset = currentViewState.preset.copy(type = type))
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
                preset = currentViewState.preset.copy(value = filteredValue),
                validationError = currentViewState.validationError.filterNot { it == AddPresetValidationError.EMPTY_AMOUNT })
        }
    }

    private fun changeNotes(value: String) {
        _viewState.update { currentViewState ->
            currentViewState.copy(preset = currentViewState.preset.copy(notes = value))
        }
    }

    private fun changeTitle(value: String) {
        _viewState.update { currentViewState ->
            currentViewState.copy(
                preset = currentViewState.preset.copy(title = value)
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
            currentViewState.copy(preset = currentViewState.preset.copy(currency = currency))
        }
    }

    private fun submit() {
        _viewState.update {
            it.copy(
                status = AddPresetUiStatus.Idle, validationError = emptyList()
            )
        }

        val currentData = _viewState.value
        val validationErrors =
            performValidation(currentData.preset, currentData.preset.type)

        if (validationErrors.isNotEmpty()) {
            _viewState.update {
                it.copy(
                    validationError = validationErrors,
                    status = AddPresetUiStatus.ValidationError
                )
            }

            return
        }

        viewModelScope.launch {
            _viewState.value.preset.let { presetToSubmit ->
                addPresetUseCase.invoke(
                    presetToSubmit,
                )
                    .onStart {
                        _viewState.update { it.copy(status = AddPresetUiStatus.Loading) }
                    }.catch { e ->
                        _viewState.update {
                            it.copy(
                                status = AddPresetUiStatus.Error(
                                    error = e as DomainException, lastAction = { submit() })
                            )
                        }
                    }.collect {
                        clearData()

                        _viewState.update { it.copy(status = AddPresetUiStatus.Success) }
                    }
            }
        }
    }

    private fun performValidation(
        preset: TransactionPresetCreation, type: TransactionType
    ): List<AddPresetValidationError> {
        val errors = mutableListOf<AddPresetValidationError>()

        val parsedValue = preset.value.toDoubleOrNull()
        if (parsedValue == null || parsedValue <= 0.0) {
            errors.add(AddPresetValidationError.EMPTY_AMOUNT)
        }

        return errors
    }
}