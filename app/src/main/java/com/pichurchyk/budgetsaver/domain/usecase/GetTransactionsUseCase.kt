package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionsByCurrency
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow

interface GetTransactionsUseCase {
    suspend fun invoke(currency: String): Flow<List<Transaction>>
}

internal class GetTransactionsUseCaseImpl(
    private val repository: TransactionsRepository
) : GetTransactionsUseCase {
    override suspend fun invoke(currency: String) = repository.getTransactions(currency)

}