package com.pichurchyk.budgetsaver.data.repository

import com.pichurchyk.budgetsaver.data.preferences.SystemPreferences
import com.pichurchyk.budgetsaver.domain.repository.SystemRepository
import com.pichurchyk.budgetsaver.ui.screen.themeselector.AppThemeOption
import kotlinx.coroutines.flow.Flow

class SystemRepositoryImpl(
    private val systemPreferences: SystemPreferences
): SystemRepository {
    override suspend fun loadSelectedAppTheme(): Flow<AppThemeOption> = systemPreferences.getAppTheme()

    override suspend fun setAppTheme(option: AppThemeOption) = systemPreferences.setAppTheme(option)
}