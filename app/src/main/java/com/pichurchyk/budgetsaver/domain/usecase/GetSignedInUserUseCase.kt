package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.user.User
import com.pichurchyk.budgetsaver.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface GetSignedInUserUseCase {
    suspend fun invoke(): Flow<User?>
}

internal class GetSignedInUserUseCaseImpl(
    private val repository: AuthRepository
) : GetSignedInUserUseCase {
    override suspend fun invoke() = flow {
        try {
            emit(repository.getUser())
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
    }
}