package com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.usecase.DeleteCategoryUseCase
import com.pichurchyk.budgetsaver.domain.usecase.GetSignedInUserUseCase
import com.pichurchyk.budgetsaver.domain.usecase.GetTransactionsCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getSignedInUserUseCase: GetSignedInUserUseCase,
    private val getCategoriesUseCase: GetTransactionsCategoriesUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
) : ViewModel() {

    private val _userViewState: MutableStateFlow<ProfileUserViewState> = MutableStateFlow(
        ProfileUserViewState()
    )
    val userViewState = _userViewState.asStateFlow()

    private val _categoriesViewState: MutableStateFlow<ProfileCategoriesViewState> =
        MutableStateFlow(
            ProfileCategoriesViewState()
        )
    val categoriesViewState = _categoriesViewState.asStateFlow()

    private fun initLoad() {
        loadUserData()
        loadCategories()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            getSignedInUserUseCase.invoke()
                .onStart {
                    _userViewState.update { currentState ->
                        currentState.copy(status = ProfileUserUiStatus.Loading)
                    }
                }
                .catch { e ->
                    _userViewState.update { currentState ->
                        currentState.copy(
                            status = ProfileUserUiStatus.Error(
                                error = e as DomainException,
                                lastAction = { loadUserData() }
                            )
                        )
                    }
                }
                .collect { user ->
                    _userViewState.update { currentState ->
                        currentState.copy(status = ProfileUserUiStatus.Idle, userData = user)
                    }
                }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase.invoke()
                .onStart {
                    _categoriesViewState.update { currentState ->
                        currentState.copy(status = ProfileCategoriesUiStatus.Loading)
                    }
                }
                .catch { e ->
                    _categoriesViewState.update { currentState ->
                        currentState.copy(
                            status = ProfileCategoriesUiStatus.Error(
                                error = e as DomainException,
                                lastAction = { loadUserData() }
                            )
                        )
                    }
                }
                .collect { categories ->
                    _categoriesViewState.update { currentState ->
                        currentState.copy(
                            status = ProfileCategoriesUiStatus.Idle,
                            categories = categories
                        )
                    }
                }
        }
    }

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.ChangeSearchCategory -> {
                changeSearchCategory(intent.value)
            }

            is ProfileIntent.DeleteCategory -> {
                deleteCategory(intent.categoryId)
            }

            is ProfileIntent.InitLoad -> {
                initLoad()
            }
        }
    }

    private fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            deleteCategoryUseCase.invoke(categoryId)
                .catch { e ->
                    _categoriesViewState.update { currentState ->
                        currentState.copy(
                            status = ProfileCategoriesUiStatus.Error(
                                error = e as DomainException,
                                lastAction = { deleteCategory(categoryId) }
                            )
                        )
                    }
                }
                .collect {
                    val updatedCategories =
                        _categoriesViewState.value.categories.filter { it.uuid != categoryId }

                    _categoriesViewState.update { currentState ->
                        currentState.copy(
                            categories = updatedCategories,
                            status = ProfileCategoriesUiStatus.Idle
                        )
                    }
                }
        }
    }

    private fun changeSearchCategory(value: String) {
        _categoriesViewState.update { currentState ->
            currentState.copy(
                search = value
            )
        }
    }
}