package com.pichurchyk.budgetsaver.ui.screen.preset.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.preset.TransactionPreset
import com.pichurchyk.budgetsaver.domain.model.preset.TransactionPresetCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.ui.screen.transaction.add.viewmodel.AddTransactionValidationError
import java.util.Currency

data class AddPresetViewState(
    val preset: TransactionPresetCreation = TransactionPresetCreation(),
    val allCurrencies: List<Currency>? = null,
    val currenciesSearch: String = "",

    val validationError: List<AddPresetValidationError> = emptyList(),

    val status: AddPresetUiStatus = AddPresetUiStatus.Idle,

    val saveAsPreset: Boolean = false
) {
    val filteredCurrencies: List<Currency>
        get() =
            allCurrencies
                ?.filter {
                    it.currencyCode.lowercase().contains(currenciesSearch.lowercase()) ||
                            it.displayName.lowercase().contains(currenciesSearch.lowercase())
                }
                ?.sortedByDescending { it == preset.currency } ?: emptyList()
}

sealed interface AddPresetUiStatus {

    object Idle : AddPresetUiStatus

    object Loading : AddPresetUiStatus

    object Success : AddPresetUiStatus

    object ValidationError: AddPresetUiStatus

    data class Error(
        val error: DomainException,
        val lastAction: () -> Unit
    ) : AddPresetUiStatus
}