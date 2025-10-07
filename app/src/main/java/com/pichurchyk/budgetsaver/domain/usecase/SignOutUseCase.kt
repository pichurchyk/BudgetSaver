package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface SignOutUseCase {
    suspend fun invoke(): Flow<Unit>
}

internal class SignOutUseCaseImpl(
    private val repository: AuthRepository
) : SignOutUseCase {
    override suspend fun invoke() = flow {
        try {
            emit(repository.signOut())
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
    }

}