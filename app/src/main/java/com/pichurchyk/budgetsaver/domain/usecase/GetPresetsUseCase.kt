package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionPreset
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow

interface GetPresetsUseCase {
    suspend fun invoke(): Flow<List<TransactionPreset>>
}

internal class GetPresetsUseCaseImpl(
    private val repository: TransactionsRepository
) : GetPresetsUseCase {
    override suspend fun invoke() = repository.getPresets()

}