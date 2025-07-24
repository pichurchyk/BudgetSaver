package com.pichurchyk.budgetsaver.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.pichurchyk.budgetsaver.ui.screen.themeselector.AppThemeOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SystemPreferences(
    private val appContext: Context
) {
    private val Context.dataStore by preferencesDataStore(TITLE)

    suspend fun setAppTheme(option: AppThemeOption) {
        appContext.dataStore.edit { preferences ->
            preferences[KEY_APP_THEME] = option.name
        }
    }

    fun getAppTheme(): Flow<AppThemeOption> {
        return appContext.dataStore.data.map { preferences ->
            AppThemeOption.valueOf(preferences[KEY_APP_THEME] ?: AppThemeOption.SYSTEM.name)
        }
    }

    companion object {
        private const val TITLE = "SystemPreferences"

        private val KEY_APP_THEME = stringPreferencesKey("KEY_APP_THEME")
    }
}