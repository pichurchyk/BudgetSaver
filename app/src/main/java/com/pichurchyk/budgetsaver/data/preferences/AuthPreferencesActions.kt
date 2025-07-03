package com.pichurchyk.budgetsaver.data.preferences

import kotlinx.coroutines.flow.Flow

interface AuthPreferencesActions {
    suspend fun setAccessToken(accessToken: String?)
    suspend fun setRefreshToken(refreshToken: String?)
    suspend fun setUserUid(uid: String?)

    suspend fun getAccessToken(): String
    suspend fun getRefreshToken(): String
    suspend fun getUserUid(): Flow<String>
}