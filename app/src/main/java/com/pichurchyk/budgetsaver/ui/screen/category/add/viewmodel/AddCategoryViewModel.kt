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
            is AddCategoryIntent.ChangeColor -> {}
            is AddCategoryIntent.ChangeEmoji -> {
                changeEmoji(intent.value)
            }

            is AddCategoryIntent.ChangeTitle -> {}
        }
    }

    private fun changeEmoji(value: String) {
        _viewState.update { currentState ->
            currentState.copy(model = currentState.model.copy(emoji = value))
        }
    }
}