package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionsByCurrency
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow

interface GetTransactionsUseCase {
    suspend fun invoke(): Flow<List<TransactionsByCurrency>>
}

internal class GetTransactionsUseCaseImpl(
    private val repository: TransactionsRepository
) : GetTransactionsUseCase {
    override suspend fun invoke() = repository.getTransactions()

}