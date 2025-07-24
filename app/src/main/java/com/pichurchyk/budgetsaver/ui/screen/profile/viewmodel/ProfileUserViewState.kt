package com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.User

data class ProfileUserViewState(
    val userData: User? = null,
    val status: ProfileUiStatus = ProfileUiStatus.Idle
)

sealed interface ProfileUiStatus {

    object Idle : ProfileUiStatus

    object Loading : ProfileUiStatus

    data class Error(
        val error: DomainException,
        val lastAction: () -> Unit
    ) : ProfileUiStatus
}