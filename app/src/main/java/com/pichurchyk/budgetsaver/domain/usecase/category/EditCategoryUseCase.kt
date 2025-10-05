package com.pichurchyk.budgetsaver.domain.usecase.category

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategoryCreation
import com.pichurchyk.budgetsaver.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface EditCategoryUseCase {
    suspend fun invoke(categoryId: String, category: TransactionCategoryCreation): Flow<Unit>
}

internal class EditCategoryUseCaseImpl(
    private val repository: TransactionsRepository
) : EditCategoryUseCase {
    override suspend fun invoke(categoryId: String, category: TransactionCategoryCreation) = flow {
        try {
            emit(repository.editCategory(categoryId, category))
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
    }
}