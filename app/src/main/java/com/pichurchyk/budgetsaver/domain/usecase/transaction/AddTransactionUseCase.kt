package com.pichurchyk.budgetsaver.domain.usecase.transaction

import com.pichurchyk.budgetsaver.data.ext.toPresetCreation
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCreation
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AddTransactionUseCase {
    suspend fun invoke(transaction: TransactionCreation, saveAsPreset: Boolean): Flow<Unit>
}

internal class AddTransactionUseCaseImpl(
    private val repository: TransactionsRepository
) : AddTransactionUseCase {
    override suspend fun invoke(transaction: TransactionCreation, saveAsPreset: Boolean): Flow<Unit> = flow {
        try {
            coroutineScope {
                val transactionDeferred = async { repository.addTransaction(transaction) }

                val presetDeferred = if (saveAsPreset) {
                    async { repository.addPreset(transaction.toPresetCreation()) }
                } else {
                    null
                }

                transactionDeferred.await()
                presetDeferred?.await()
            }

            emit(Unit)
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
    }
}