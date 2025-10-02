package com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pichurchyk.budgetsaver.di.DomainException
import com.pichurchyk.budgetsaver.domain.usecase.category.DeleteCategoryUseCase
import com.pichurchyk.budgetsaver.domain.usecase.preset.DeletePresetUseCase
import com.pichurchyk.budgetsaver.domain.usecase.preset.GetPresetsUseCase
import com.pichurchyk.budgetsaver.domain.usecase.GetSignedInUserUseCase
import com.pichurchyk.budgetsaver.domain.usecase.category.GetTransactionsCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getSignedInUserUseCase: GetSignedInUserUseCase,
    private val getCategoriesUseCase: GetTransactionsCategoriesUseCase,
    private val getPresetsUseCase: GetPresetsUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val deletePresetUseCase: DeletePresetUseCase,
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

    private val _presetsViewState: MutableStateFlow<ProfilePresetsViewState> =
        MutableStateFlow(
            ProfilePresetsViewState()
        )
    val presetsViewState = _presetsViewState.asStateFlow()

    private fun initLoad() {
        loadUserData()
        loadCategories()
        loadPresets()
    }

    private fun loadPresets() {
        viewModelScope.launch {
            getPresetsUseCase.invoke()
                .onStart {
                    _presetsViewState.update { currentState ->
                        currentState.copy(status = ProfilePresetsUiStatus.Loading)
                    }
                }
                .catch { e ->
                    _presetsViewState.update { currentState ->
                        currentState.copy(
                            status = ProfilePresetsUiStatus.Error(
                                error = e as DomainException,
                                lastAction = { loadUserData() }
                            )
                        )
                    }
                }
                .collect { presets ->
                    _presetsViewState.update { currentState ->
                        currentState.copy(
                            status = ProfilePresetsUiStatus.Idle,
                            presets = presets
                        )
                    }
                }
        }
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

            is ProfileIntent.ChangeSearchPreset -> {
                changeSearchPreset(intent.value)
            }

            is ProfileIntent.DeletePreset -> {
                deletePreset(intent.presetId)
            }
        }
    }

    private fun deletePreset(presetId: String) {
        viewModelScope.launch {
            deletePresetUseCase.invoke(presetId)
                .catch { e ->
                    _presetsViewState.update { currentState ->
                        currentState.copy(
                            status = ProfilePresetsUiStatus.Error(
                                error = e as DomainException,
                                lastAction = { deleteCategory(presetId) }
                            )
                        )
                    }
                }
                .collect {
                    val updatedPresets =
                        _presetsViewState.value.presets.filter { it.uuid != presetId }

                    _presetsViewState.update { currentState ->
                        currentState.copy(
                            presets = updatedPresets,
                            status = ProfilePresetsUiStatus.Idle
                        )
                    }
                }
        }
    }

    private fun changeSearchPreset(value: String) {
        _presetsViewState.update { currentState ->
            currentState.copy(
                search = value
            )
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