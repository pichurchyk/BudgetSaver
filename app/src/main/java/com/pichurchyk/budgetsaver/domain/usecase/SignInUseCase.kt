package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.domain.model.SignInResult
import com.pichurchyk.budgetsaver.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

interface SignInUseCase {
    suspend fun invoke(googleIdToken: String): Flow<SignInResult>
}

internal class SignInUseCaseImpl(
    private val repository: AuthRepository
) : SignInUseCase {
    override suspend fun invoke(googleIdToken: String) = repository.signIn(googleIdToken)

}