package com.pichurchyk.budgetsaver.ui.screen.themeselector.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.domain.repository.SystemRepository
import com.pichurchyk.budgetsaver.ui.screen.themeselector.AppThemeOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppThemeSelectorViewModel(
    private val systemRepository: SystemRepository
): ViewModel() {

    private val _viewState: MutableStateFlow<AppThemeSelectorViewState> =
        MutableStateFlow(AppThemeSelectorViewState.Loading)

    val viewState = _viewState.asStateFlow()

    init {
        loadData()
    }

    fun handleIntent(intent: AppThemeSelectorIntent) {
        when (intent) {
            is AppThemeSelectorIntent.Select -> changeSelectedOption(intent.option)
        }
    }

    private fun changeSelectedOption(option: AppThemeOption) {
        if (_viewState.value !is AppThemeSelectorViewState.Idle) return

        viewModelScope.launch {
            systemRepository.setAppTheme(option)
        }

        _viewState.update { currentState ->
            (currentState as AppThemeSelectorViewState.Idle).copy(selectedOption = option)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            systemRepository
                .loadSelectedAppTheme()
                .onStart {
                    _viewState.update {
                        AppThemeSelectorViewState.Loading
                    }
                }
                .catch {
                    _viewState.update {
                        AppThemeSelectorViewState.Idle(AppThemeOption.SYSTEM)
                    }
                }
                .collect { option ->
                    _viewState.update {
                        AppThemeSelectorViewState.Idle(option)
                    }
                }
        }
    }
}