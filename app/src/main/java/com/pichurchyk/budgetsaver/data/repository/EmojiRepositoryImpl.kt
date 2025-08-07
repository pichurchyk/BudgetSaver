package com.pichurchyk.budgetsaver.data.repository

import android.content.Context
import com.pichurchyk.budgetsaver.domain.model.Emoji
import com.pichurchyk.budgetsaver.domain.repository.EmojiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class EmojiRepositoryImpl(
    private val context: Context,
) : EmojiRepository {
    
    private var emojis: List<Emoji> = emptyList()
    private var isLoaded = false

    override suspend fun loadEmojis(): List<Emoji> = withContext(Dispatchers.IO) {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true // Optional: allows relaxed parsing
            coerceInputValues = true // Optional: coerces null values to defaults
        }


        if (!isLoaded) {
            try {
                val jsonString = context.assets.open("emojis.json")
                    .bufferedReader()
                    .use { it.readText() }

                emojis = json.decodeFromString<List<Emoji>>(jsonString)
                isLoaded = true
            } catch (e: Exception) {
                e.printStackTrace()
                emojis = emptyList()
            }
        }
        emojis
    }

    override fun getEmojisByCategory(category: String): List<Emoji> =
        emojis.filter { it.category == category }
    
    override fun searchEmojis(query: String): List<Emoji> {
        val lowercaseQuery = query.lowercase()
        return emojis.filter { emoji ->
            emoji.description.lowercase().contains(lowercaseQuery) ||
            emoji.aliases.any { it.lowercase().contains(lowercaseQuery) } ||
            emoji.tags.any { it.lowercase().contains(lowercaseQuery) }
        }
    }
}