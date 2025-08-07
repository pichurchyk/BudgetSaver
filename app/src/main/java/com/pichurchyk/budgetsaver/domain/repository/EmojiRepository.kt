package com.pichurchyk.budgetsaver.domain.repository

import com.pichurchyk.budgetsaver.domain.model.Emoji

interface EmojiRepository {
    suspend fun loadEmojis(): List<Emoji>
    fun getEmojisByCategory(category: String): List<Emoji>
    fun searchEmojis(query: String): List<Emoji>
}