package com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.User

sealed class ProfileUserUiStatus {
    data object Idle : ProfileUserUiStatus()
    data object Loading : ProfileUserUiStatus()
    data class Error(
        val error: DomainException,
        val lastAction: () -> Unit
    ) : ProfileUserUiStatus()
}

data class ProfileUserViewState(
    val status: ProfileUserUiStatus = ProfileUserUiStatus.Idle,
    val userData: User? = null
)