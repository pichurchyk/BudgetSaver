package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategoryCreation
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AddCategoryUseCase {
    suspend fun invoke(category: TransactionCategoryCreation): Flow<Unit>
}

internal class AddCategoryUseCaseImpl(
    private val repository: TransactionsRepository
) : AddCategoryUseCase {
    override suspend fun invoke(category: TransactionCategoryCreation) = flow {
        try {
            emit(repository.addCategory(category))
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
    }
}