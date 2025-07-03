package com.pichurchyk.budgetsaver.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AuthPreferences(
    private val appContext: Context
): AuthPreferencesActions {
    private val Context.dataStore by preferencesDataStore(TITLE)

    override suspend fun setAccessToken(accessToken: String?) {
        appContext.dataStore.edit { preferences ->
            val newValue = if (accessToken.isNullOrEmpty()) "" else accessToken
            preferences[KEY_ACCESS_TOKEN] = newValue
        }
    }

    override suspend fun setRefreshToken(refreshToken: String?) {
        appContext.dataStore.edit { preferences ->
            val newValue = if (refreshToken.isNullOrEmpty()) "" else refreshToken
            preferences[KEY_REFRESH_TOKEN] = newValue
        }
    }

    override suspend fun setUserUid(uid: String?) {
        appContext.dataStore.edit { preferences ->
            val newValue = if (uid.isNullOrEmpty()) "" else uid
            preferences[KEY_USER_UID] = newValue
        }
    }

    override suspend fun getUserUid(): Flow<String> {
        return appContext.dataStore.data.map { preferences ->
            preferences[KEY_USER_UID] ?: ""
        }
    }

    override suspend fun getAccessToken(): String {
        return appContext.dataStore.data.first().let { preferences ->
            preferences[KEY_ACCESS_TOKEN] ?: ""
        }
    }

    override suspend fun getRefreshToken(): String {
        return appContext.dataStore.data.first().let { preferences ->
            preferences[KEY_REFRESH_TOKEN] ?: ""
        }
    }

    companion object {
        private const val TITLE = "AuthPreferences"

        private val KEY_ACCESS_TOKEN = stringPreferencesKey("KEY_ACCESS_TOKEN")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("KEY_REFRESH_TOKEN")
        private val KEY_USER_UID = stringPreferencesKey("KEY_USER_UID")
    }
}