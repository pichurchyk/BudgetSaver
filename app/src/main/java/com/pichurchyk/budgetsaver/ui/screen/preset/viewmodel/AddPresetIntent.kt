package com.pichurchyk.budgetsaver.ui.screen.preset.viewmodel

import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.preset.TransactionPreset
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import java.util.Currency

sealed class AddPresetIntent {
    data object Submit: AddPresetIntent()

    data class ChangeValue(val value: String): AddPresetIntent()

    data class ChangeTitle(val value: String): AddPresetIntent()

    data class ChangeNotes(val value: String): AddPresetIntent()

    data class ChangeCurrency(val currency: Currency): AddPresetIntent()

    data class SearchCurrency(val value: String): AddPresetIntent()

    data class ChangeType(val value: TransactionType): AddPresetIntent()

    data class ChangeCategory(val value: TransactionCategory?): AddPresetIntent()

    data object ClearData: AddPresetIntent()

    data object DismissNotification: AddPresetIntent()
}