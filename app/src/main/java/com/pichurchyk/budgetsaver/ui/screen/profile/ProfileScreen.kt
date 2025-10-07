package com.pichurchyk.budgetsaver.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.ui.common.Loader
import com.pichurchyk.budgetsaver.ui.ext.doOnClick
import com.pichurchyk.budgetsaver.ui.ext.imePaddingWithoutNavBars
import com.pichurchyk.budgetsaver.ui.screen.currency.FavoriteCurrenciesSelector
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileCategoriesViewState
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileIntent
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfilePresetsViewState
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileUserViewState
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileViewModel
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.SignOutViewState
import com.pichurchyk.budgetsaver.ui.screen.themeselector.AppThemeSelector
import com.pichurchyk.budgetsaver.ui.theme.red
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    openAddCategory: () -> Unit,
    openEditCategory: (categoryId: String) -> Unit,
    openAddPreset: () -> Unit,
    openAuthScreen: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    val userViewState by viewModel.userViewState.collectAsState()
    val categoriesViewState by viewModel.categoriesViewState.collectAsState()
    val presetsViewState by viewModel.presetsViewState.collectAsState()
    val signOutViewState by viewModel.signOutViewState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(ProfileIntent.InitLoad)
    }

    LaunchedEffect(signOutViewState) {
        if (signOutViewState is SignOutViewState.SignedOut) {
            openAuthScreen()
        }
    }

    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (signOutViewState is SignOutViewState.Loading || signOutViewState is SignOutViewState.SignedOut) {
            Loader(modifier = Modifier.size(40.dp))
        } else {
            Content(
                profileViewState = userViewState,
                categoriesViewState = categoriesViewState,
                callViewModel = { viewModel.handleIntent(it) },
                onAddCategoryClick = openAddCategory,
                presetsViewState = presetsViewState,
                onAddPresetClick = openAddPreset,
                onEditCategoryClick = openEditCategory
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    profileViewState: ProfileUserViewState,
    categoriesViewState: ProfileCategoriesViewState,
    presetsViewState: ProfilePresetsViewState,
    callViewModel: (ProfileIntent) -> Unit,
    onAddCategoryClick: () -> Unit,
    onEditCategoryClick: (categoryId: String) -> Unit,
    onAddPresetClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(
                top = 16.dp,
            )
            .imePaddingWithoutNavBars()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ProfileCard(
            modifier = Modifier,
            viewState = profileViewState
        )

        FavoriteCurrenciesSelector(
            modifier = Modifier
        )

        ProfileCategories(
            modifier = Modifier,
            viewState = categoriesViewState,
            onChipClicked = {
                onEditCategoryClick(it.uuid)
            },
            onSearchValueChanged = {
                callViewModel.invoke(ProfileIntent.ChangeSearchCategory(it))
            },
            onDeleteChipClick = {
                callViewModel.invoke(ProfileIntent.DeleteCategory(it.uuid))
            },
            onAddCategoryClick = {
                onAddCategoryClick()
            }
        )

        ProfilePresets(
            modifier = Modifier,
            viewState = presetsViewState,
            onChipClicked = {

            },
            onSearchValueChanged = {
                callViewModel.invoke(ProfileIntent.ChangeSearchPreset(it))
            },
            onDeleteChipClick = {
                callViewModel.invoke(ProfileIntent.DeletePreset(it.uuid))
            },
            onAddPresetClick = {
                onAddPresetClick()
            }
        )

        AppThemeSelector(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                )
        )

        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(
                    bottom = WindowInsets
                        .navigationBars
                        .only(WindowInsetsSides.Bottom)
                        .asPaddingValues()
                        .calculateBottomPadding()
                            + 8.dp
                )
                .doOnClick {
                    callViewModel(ProfileIntent.SignOut)
                },
            text = stringResource(R.string.sign_out),
            color = MaterialTheme.colorScheme.error,
            fontSize = 16.sp,
        )
    }
}
