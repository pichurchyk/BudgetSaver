package com.pichurchyk.budgetsaver.domain.repository

import com.pichurchyk.budgetsaver.domain.model.SignInResult
import com.pichurchyk.budgetsaver.domain.model.user.User
import kotlinx.coroutines.flow.Flow

internal interface AuthRepository {

    suspend fun signIn(googleIdToken: String): Flow<SignInResult>

    suspend fun signOut(): Flow<Unit>

    suspend fun getUser(): User?
}