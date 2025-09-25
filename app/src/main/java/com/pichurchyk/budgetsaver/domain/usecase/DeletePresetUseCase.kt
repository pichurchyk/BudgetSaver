package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface DeletePresetUseCase {
    suspend fun invoke(presetId: String): Flow<Unit>
}

internal class DeletePresetUseCaseImpl(
    private val repository: TransactionsRepository
) : DeletePresetUseCase {
    override suspend fun invoke(presetId: String) = flow {
        try {
            emit(repository.deletePreset(presetId))
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
    }
}