package com.pichurchyk.budgetsaver.data.repository

import com.pichurchyk.budgetsaver.data.datasource.AuthDataSource
import com.pichurchyk.budgetsaver.data.ext.toUser
import com.pichurchyk.budgetsaver.domain.model.SignInResult
import com.pichurchyk.budgetsaver.domain.model.User
import com.pichurchyk.budgetsaver.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource
): AuthRepository {

    override suspend fun signIn(googleIdToken: String): Flow<SignInResult> {
        return authDataSource.signIn(googleIdToken)
    }

    override suspend fun getSignedInUser(): Flow<User?> {
        return authDataSource.getSignedInUser().map { it?.toUser() }
    }

    override suspend fun signOut(): Flow<Unit> {
        return authDataSource.signOut()
    }
}