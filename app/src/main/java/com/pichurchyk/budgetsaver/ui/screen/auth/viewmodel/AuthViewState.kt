package com.pichurchyk.budgetsaver.ui.screen.auth.viewmodel

sealed interface AuthViewState {
    data object Init: AuthViewState

    data object Loading: AuthViewState

    sealed interface Success: AuthViewState {
        data object SignedIn: Success
        data object NotSignedIn: Success
    }

    data class Error(val message: String?): AuthViewState
}