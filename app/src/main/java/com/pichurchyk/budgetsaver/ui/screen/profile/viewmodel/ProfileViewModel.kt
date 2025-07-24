package com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.usecase.GetSignedInUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getSignedInUserUseCase: GetSignedInUserUseCase,
) : ViewModel() {

    private val _viewState: MutableStateFlow<ProfileUserViewState> = MutableStateFlow(
        ProfileUserViewState()
    )
    val viewState = _viewState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            getSignedInUserUseCase.invoke()
                .onStart {
                    _viewState.update { currentState ->
                        currentState.copy(status = ProfileUiStatus.Loading)
                    }
                }
                .catch { e ->
                    _viewState.update { currentState ->
                        currentState.copy(
                            status = ProfileUiStatus.Error(
                                error = e as DomainException,
                                lastAction = { loadUserData() }
                            )
                        )
                    }
                }
                .collect { user ->
                    _viewState.update { currentState ->
                        currentState.copy(status = ProfileUiStatus.Idle, userData = user)
                    }
                }
        }
    }

    fun handleIntent(intent: ProfileIntent) {

    }
}