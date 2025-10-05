package com.pichurchyk.budgetsaver.ui.screen.category.edit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.data.ext.category.toCreation
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategoryCreation
import com.pichurchyk.budgetsaver.domain.usecase.category.EditCategoryUseCase
import com.pichurchyk.budgetsaver.domain.usecase.LoadEmojisUseCase
import com.pichurchyk.budgetsaver.domain.usecase.SearchEmojiUseCase
import com.pichurchyk.budgetsaver.domain.usecase.category.GetTransactionsCategoriesUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditCategoryViewModel(
    private val categoryId: String,
    private val loadEmojisUseCase: LoadEmojisUseCase,
    private val searchEmojiUseCase: SearchEmojiUseCase,
    private val editCategoryUseCase: EditCategoryUseCase,
    private val getTransactionsCategoriesUseCase: GetTransactionsCategoriesUseCase
) : ViewModel() {

    private val _viewState = MutableStateFlow(EditCategoryViewState())
    val viewState = _viewState.asStateFlow()

    private val _notificationEvent = Channel<EditCategoryNotification>(Channel.BUFFERED)
    val notificationEvent = _notificationEvent.receiveAsFlow()

    init {
        loadEmojis()
        loadCategory()
    }

    fun handleIntent(intent: EditCategoryIntent) {
        when (intent) {
            is EditCategoryIntent.ChangeColor -> changeColor(intent.value)
            is EditCategoryIntent.ChangeEmoji -> changeEmoji(intent.value)
            is EditCategoryIntent.ChangeSearchEmojiValue -> changeSearchValueEmoji(intent.value)
            is EditCategoryIntent.ChangeTitle -> changeTitle(intent.value)
            is EditCategoryIntent.Submit -> submit()
        }
    }

    private fun submit() {
        viewModelScope.launch {
            editCategoryUseCase.invoke(categoryId, viewState.value.model)
                .onStart {
                    _viewState.update { currentState ->
                        currentState.copy(status = EditCategoryUiStatus.Loading)
                    }
                }
                .catch {
                    _viewState.update { currentState ->
                        currentState.copy(status = EditCategoryUiStatus.Idle)
                    }

                    _notificationEvent.send(
                        EditCategoryNotification.Error(
                            error = it as DomainException,
                            lastAction = { submit() }
                        )
                    )
                }
                .collect {
                    _notificationEvent.send(EditCategoryNotification.Success)
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
                        currentState.copy(availableEmojis = emojis)
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
                            model = currentState.model.copy(
                                emoji = currentState.model.emoji.ifEmpty { emojis.random().emoji }
                            )
                        )
                    }
                }
        }
    }

    private fun loadCategory() {
        viewModelScope.launch {
            getTransactionsCategoriesUseCase.invoke(listOf(categoryId))
                .onStart {
                    _viewState.update { currentState ->
                        currentState.copy(status = EditCategoryUiStatus.Loading)
                    }
                }
                .catch {
                    _viewState.update { currentState ->
                        currentState.copy(status = EditCategoryUiStatus.Idle)
                    }

                    _notificationEvent.send(
                        EditCategoryNotification.Error(
                            error = it as DomainException,
                            lastAction = { submit() }
                        )
                    )
                }
                .collect { response ->
                    response.firstOrNull()?.let { category ->
                        _viewState.update { currentState ->
                            currentState.copy(
                                model = category.toCreation()
                            )
                        }
                    }
                }
        }
    }
}
