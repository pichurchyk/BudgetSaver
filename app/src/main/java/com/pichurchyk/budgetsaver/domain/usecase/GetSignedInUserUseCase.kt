package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.domain.model.User
import com.pichurchyk.budgetsaver.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

interface GetSignedInUserUseCase {
    suspend fun invoke(): Flow<User?>
}

internal class GetSignedInUserUseCaseImpl(
    private val repository: AuthRepository
) : GetSignedInUserUseCase {
    override suspend fun invoke() = repository.getSignedInUser()

}