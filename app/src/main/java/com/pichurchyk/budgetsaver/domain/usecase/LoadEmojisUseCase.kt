package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.Emoji
import com.pichurchyk.budgetsaver.domain.repository.EmojiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface LoadEmojisUseCase {
    suspend fun invoke(): Flow<List<Emoji>>
}

internal class LoadEmojisUseCaseImpl(
    private val repository: EmojiRepository
) : LoadEmojisUseCase {
    override suspend fun invoke(): Flow<List<Emoji>> = flow {
        try {
            emit(repository.loadEmojis())
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
    }

}