package com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.preset.TransactionPreset

sealed class ProfilePresetsUiStatus {
    data object Idle : ProfilePresetsUiStatus()
    data object Loading : ProfilePresetsUiStatus()
    data class Error(
        val error: DomainException,
        val lastAction: () -> Unit
    ) : ProfilePresetsUiStatus()
}

data class ProfilePresetsViewState(
    val status: ProfilePresetsUiStatus = ProfilePresetsUiStatus.Idle,
    val presets: List<TransactionPreset> = emptyList(),
    val search: String = ""
) {
    val filteredPresets: List<TransactionPreset>
        get() = presets.filter { preset ->
            val title = preset.title
            when {
                title == null && search.isEmpty() -> true
                title == null && search.isNotEmpty() -> false
                else -> title?.lowercase()?.contains(search.lowercase()) == true
            }
        }
}