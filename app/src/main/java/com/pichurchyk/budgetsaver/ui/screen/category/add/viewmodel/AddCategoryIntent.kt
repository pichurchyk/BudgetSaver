package com.pichurchyk.budgetsaver.ui.screen.category.add.viewmodel

sealed interface AddCategoryIntent {
    data class ChangeTitle(val value: String) : AddCategoryIntent

    data class ChangeEmoji(val value: String) : AddCategoryIntent
    data class ChangeSearchEmojiValue(val value: String) : AddCategoryIntent

    data class ChangeColor(val value: String) : AddCategoryIntent

    data object Submit: AddCategoryIntent
}