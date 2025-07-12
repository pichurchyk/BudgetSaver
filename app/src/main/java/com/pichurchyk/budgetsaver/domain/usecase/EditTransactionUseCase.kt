package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface EditTransactionUseCase {
    suspend fun invoke(transactionId: String, transaction: TransactionCreation): Flow<Unit>
}

internal class EditTransactionUseCaseImpl(
    private val repository: TransactionsRepository
) : EditTransactionUseCase {
    override suspend fun invoke(transactionId: String, transaction: TransactionCreation): Flow<Unit> = flow {
        try {
            repository.editTransaction(transactionId, transaction)

            emit(Unit)
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
    }
}