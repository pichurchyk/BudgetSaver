package com.pichurchyk.budgetsaver.ui.screen.category.add.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategoryCreation
import com.pichurchyk.budgetsaver.domain.usecase.AddCategoryUseCase
import com.pichurchyk.budgetsaver.domain.usecase.LoadEmojisUseCase
import com.pichurchyk.budgetsaver.domain.usecase.SearchEmojiUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddCategoryViewModel(
    private val loadEmojisUseCase: LoadEmojisUseCase,
    private val searchEmojiUseCase: SearchEmojiUseCase,
    private val addCategoryUseCase: AddCategoryUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow(AddCategoryViewState())
    val viewState = _viewState.asStateFlow()

    private val _notificationEvent = Channel<AddCategoryNotification>(Channel.BUFFERED)
    val notificationEvent = _notificationEvent.receiveAsFlow()

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

            is AddCategoryIntent.Submit -> {
                submit()
            }
        }
    }

    private fun submit() {
        viewModelScope.launch {
            addCategoryUseCase.invoke(viewState.value.model)
                .onStart {
                    _viewState.update { currentState ->
                        currentState.copy(status = AddCategoryUiStatus.Loading)
                    }
                }
                .catch {
                    _viewState.update { currentState ->
                        currentState.copy(status = AddCategoryUiStatus.Idle)
                    }

                    _notificationEvent.send(
                        AddCategoryNotification.Error(
                            error = it as DomainException,
                            lastAction = {
                                submit()
                            }
                        )
                    )
                }
                .collect {
                    _notificationEvent.send(
                        AddCategoryNotification.Success
                    )

                    resetData()
                }
        }
    }

    private fun resetData() {
        _viewState.update { currentState ->
            currentState.copy(
                model = TransactionCategoryCreation(
                    emoji = currentState.availableEmojis.random().emoji
                )
            )
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