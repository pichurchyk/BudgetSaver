package com.pichurchyk.budgetsaver.ui.screen.themeselector.viewmodel

import com.pichurchyk.budgetsaver.ui.screen.themeselector.AppThemeOption

sealed class AppThemeSelectorViewState {
    data object Loading: AppThemeSelectorViewState()

    data class Idle(val selectedOption: AppThemeOption): AppThemeSelectorViewState()
}