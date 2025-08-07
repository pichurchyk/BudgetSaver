package com.pichurchyk.budgetsaver.ui.screen.category.add.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.domain.usecase.LoadEmojisUseCase
import com.pichurchyk.budgetsaver.domain.usecase.SearchEmojiUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddCategoryViewModel(
    private val loadEmojisUseCase: LoadEmojisUseCase,
    private val searchEmojiUseCase: SearchEmojiUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow(AddCategoryViewState())
    val viewState = _viewState.asStateFlow()

    init {
        loadEmojis()
    }

    fun handleIntent(intent: AddCategoryIntent) {
        when (intent) {
            is AddCategoryIntent.ChangeColor -> {
                changeColor(intent.value)
            }

            is AddCategoryIntent.ChangeEmoji -> {
                changeEmoji(intent.value)
            }

            is AddCategoryIntent.ChangeSearchEmojiValue -> {
                changeSearchValueEmoji(intent.value)
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

    private fun changeSearchValueEmoji(value: String) {
        _viewState.update { currentState ->
            currentState.copy(searchEmojisValue = value)
        }

        viewModelScope.launch {
            searchEmojiUseCase.invoke(value)
                .collect { emojis ->
                    _viewState.update { currentState ->
                        currentState.copy(
                            availableEmojis = emojis,
                        )
                    }
                }
        }
    }

    private fun changeTitle(value: String) {
        _viewState.update { currentState ->
            currentState.copy(model = currentState.model.copy(title = value))
        }
    }

    private fun loadEmojis() {
        viewModelScope.launch {
            loadEmojisUseCase.invoke()
                .collect { emojis ->
                    _viewState.update { currentState ->
                        currentState.copy(
                            availableEmojis = emojis,
                            model = currentState.model.copy(emoji = emojis.random().emoji)
                        )
                    }
                }
        }
    }
}