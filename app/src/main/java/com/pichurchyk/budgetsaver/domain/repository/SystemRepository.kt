package com.pichurchyk.budgetsaver.domain.repository

import com.pichurchyk.budgetsaver.ui.screen.themeselector.AppThemeOption
import kotlinx.coroutines.flow.Flow

interface SystemRepository {
    suspend fun loadSelectedAppTheme(): Flow<AppThemeOption>
    suspend fun setAppTheme(option: AppThemeOption)
}