package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface DeleteCategoryUseCase {
    suspend fun invoke(categoryId: String): Flow<Unit>
}

internal class DeleteCategoryUseCaseImpl(
    private val repository: TransactionsRepository
) : DeleteCategoryUseCase {
    override suspend fun invoke(categoryId: String) = flow {
        try {
            emit(repository.deleteCategory(categoryId))
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
    }
}