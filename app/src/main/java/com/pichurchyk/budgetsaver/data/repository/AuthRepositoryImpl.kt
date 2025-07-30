package com.pichurchyk.budgetsaver.data.repository

import com.pichurchyk.budgetsaver.data.datasource.AuthDataSource
import com.pichurchyk.budgetsaver.data.datasource.SessionManager
import com.pichurchyk.budgetsaver.data.ext.user.toUser
import com.pichurchyk.budgetsaver.domain.model.SignInResult
import com.pichurchyk.budgetsaver.domain.model.user.User
import com.pichurchyk.budgetsaver.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

internal class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource,
    private val sessionManager: SessionManager
): AuthRepository {

    override suspend fun signIn(googleIdToken: String): Flow<SignInResult> {
        return authDataSource.signIn(googleIdToken)
    }

    override suspend fun signOut(): Flow<Unit> {
        return authDataSource.signOut()
    }

    override suspend fun getUser(): User? {
        val user = authDataSource.getUser()?.toUser()

        user?.let { sessionManager.setUser(user) }

        return user
    }
}