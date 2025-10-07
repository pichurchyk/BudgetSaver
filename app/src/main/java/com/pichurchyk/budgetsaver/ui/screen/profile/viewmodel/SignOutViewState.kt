package com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel

import com.pichurchyk.budgetsaver.di.DomainException

sealed class SignOutViewState {
    data object Idle: SignOutViewState()
    data object Loading : SignOutViewState()
    data object SignedOut: SignOutViewState()
    data class Error(
        val error: DomainException,
        val lastAction: () -> Unit
    ) : SignOutViewState()
}