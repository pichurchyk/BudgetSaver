package com.pichurchyk.budgetsaver.ui.screen.category.edit.viewmodel

sealed interface EditCategoryIntent {
    data class ChangeTitle(val value: String) : EditCategoryIntent

    data class ChangeEmoji(val value: String) : EditCategoryIntent
    data class ChangeSearchEmojiValue(val value: String) : EditCategoryIntent

    data class ChangeColor(val value: String) : EditCategoryIntent

    data object Submit: EditCategoryIntent
}
