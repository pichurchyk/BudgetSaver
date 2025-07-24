package com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel

sealed class ProfileIntent {
    data object DismissNotification: ProfileIntent()
}