package com.pichurchyk.budgetsaver.domain.usecase

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.Emoji
import com.pichurchyk.budgetsaver.domain.repository.EmojiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface SearchEmojiUseCase {
    suspend fun invoke(query: String): Flow<List<Emoji>>
}

internal class SearchEmojiUseCaseImpl(
    private val repository: EmojiRepository
) : SearchEmojiUseCase {
    override suspend fun invoke(query: String): Flow<List<Emoji>> = flow {
        try {
            emit(repository.searchEmojis(query))
        } catch (e: DomainException) {
            throw e
        } catch (e: Exception) {
            throw DomainException.UnknownApiException(cause = e)
        }
    }

}