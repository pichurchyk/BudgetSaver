package com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel

sealed class ProfileIntent {
    data class ChangeSearchCategory(val value: String): ProfileIntent()

    data class DeleteCategory(val categoryId: String): ProfileIntent()
}