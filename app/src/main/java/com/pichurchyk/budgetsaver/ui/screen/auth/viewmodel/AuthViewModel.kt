package com.pichurchyk.budgetsaver.ui.screen.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.domain.model.SignInResult
import com.pichurchyk.budgetsaver.domain.usecase.GetSignedInUserUseCase
import com.pichurchyk.budgetsaver.domain.usecase.LoadEmojisUseCase
import com.pichurchyk.budgetsaver.domain.usecase.SignInUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val getSignedInUserUseCase: GetSignedInUserUseCase,
    private val signInUseCase: SignInUseCase,
    private val loadEmojisUseCase: LoadEmojisUseCase,
) : ViewModel() {

    private val _state: MutableStateFlow<AuthViewState> = MutableStateFlow(AuthViewState.Init)
    val state = _state.asStateFlow()

    private var emojiLoadingJob: Job? = null

    init {
        // Start loading emojis immediately when ViewModel is created
        emojiLoadingJob = loadEmojis()
    }

    fun handleIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Auth -> {
                auth(intent.googleIdToken)
            }
            is AuthIntent.CheckSignedInUser -> {
                checkIfUserSignedIn()
            }
        }
    }

    private fun auth(googleIdToken: String) {
        viewModelScope.launch {
            signInUseCase.invoke(googleIdToken)
                .onStart {
                    _state.update { AuthViewState.Loading }
                }
                .catch { error ->
                    _state.update { AuthViewState.Error(error.message) }
                }
                .collect { response ->
                    when (response) {
                        is SignInResult.Success -> {
                            emojiLoadingJob?.let { job ->
                                if (job.isCompleted) {
                                    _state.update { AuthViewState.Success.SignedIn }
                                } else {
                                    _state.update { AuthViewState.Loading }
                                    job.join()

                                    _state.update { AuthViewState.Success.SignedIn }
                                }
                            }
                        }

                        is SignInResult.Error -> {
                            _state.update { AuthViewState.Error(response.errorMessage) }
                        }

                        is SignInResult.Cancelled -> {
                            checkIfUserSignedIn()
                        }
                    }
                }
        }
    }

    private fun checkIfUserSignedIn() {
        viewModelScope.launch {
            getSignedInUserUseCase.invoke()
                .catch {
                    _state.update { AuthViewState.Success.NotSignedIn }
                }
                .collect { signedInUser ->
                    if (signedInUser != null) {
                        _state.update { AuthViewState.Success.SignedIn }
                    } else {
                        _state.update { AuthViewState.Success.NotSignedIn }
                    }
                }
        }
    }

    private fun loadEmojis(): Job {
        return viewModelScope.launch {
            try {
                loadEmojisUseCase.invoke()
                    .collect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun waitForEmojisAndUpdateState(targetState: AuthViewState) {
        emojiLoadingJob?.let { job ->
            if (job.isCompleted) {
                // Emojis already loaded, update state immediately
                _state.update { targetState }
            } else {
                // Show loading while waiting for emojis
                _state.update { AuthViewState.Loading }

                // Wait for the job to complete
                job.join()

                _state.update { targetState }
            }
        } ?: run {
            // Job is null, proceed immediately
            _state.update { targetState }
        }
    }
}