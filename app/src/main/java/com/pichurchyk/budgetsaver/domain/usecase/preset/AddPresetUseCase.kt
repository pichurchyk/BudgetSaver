package com.pichurchyk.budgetsaver.domain.usecase.preset

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.preset.TransactionPresetCreation
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AddPresetUseCase {
    suspend fun invoke(preset: TransactionPresetCreation): Flow<Unit>
}

internal class AddPresetUseCaseImpl(
    private val repository: TransactionsRepository
) : AddPresetUseCase {
    override suspend fun invoke(preset: TransactionPresetCreation) = flow {
        try {
            emit(repository.addPreset(preset))
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
    }
}