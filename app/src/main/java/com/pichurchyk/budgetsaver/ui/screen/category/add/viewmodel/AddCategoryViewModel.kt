package com.pichurchyk.budgetsaver.ui.screen.category.add.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddCategoryViewModel : ViewModel() {

    private val _viewState = MutableStateFlow(AddCategoryViewState())
    val viewState = _viewState.asStateFlow()

    fun handleIntent(intent: AddCategoryIntent) {
        when (intent) {
            is AddCategoryIntent.ChangeColor -> {
                changeColor(intent.value)
            }
            is AddCategoryIntent.ChangeEmoji -> {
                changeEmoji(intent.value)
            }

            is AddCategoryIntent.ChangeTitle -> {
                changeTitle(intent.value)
            }
        }
    }

    private fun changeColor(value: String) {
        _viewState.update { currentState ->
            currentState.copy(model = currentState.model.copy(color = value))
        }
    }

    private fun changeEmoji(value: String) {
        _viewState.update { currentState ->
            currentState.copy(model = currentState.model.copy(emoji = value))
        }
    }

    private fun changeTitle(value: String) {
        _viewState.update { currentState ->
            currentState.copy(model = currentState.model.copy(title = value))
        }
    }
}