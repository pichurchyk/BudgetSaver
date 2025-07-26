package com.pichurchyk.budgetsaver.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.domain.repository.SystemRepository
import com.pichurchyk.budgetsaver.ui.screen.themeselector.AppThemeOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val systemRepository: SystemRepository
): ViewModel() {

    private val _appTheme = MutableStateFlow(AppThemeOption.SYSTEM)
    val appTheme: StateFlow<AppThemeOption> = _appTheme.asStateFlow()

    init {
        viewModelScope.launch {
            systemRepository.loadSelectedAppTheme().collect { option ->
                _appTheme.value = option
            }
        }
    }

}