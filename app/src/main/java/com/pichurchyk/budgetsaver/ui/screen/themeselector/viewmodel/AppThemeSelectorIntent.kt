package com.pichurchyk.budgetsaver.ui.screen.themeselector.viewmodel

import com.pichurchyk.budgetsaver.ui.screen.themeselector.AppThemeOption

sealed class AppThemeSelectorIntent {
    data class Select(val option: AppThemeOption): AppThemeSelectorIntent()
}