package com.pichurchyk.budgetsaver.ui.screen.profile

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.ui.screen.currency.FavoriteCurrenciesSelector
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileCategoriesViewState
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileIntent
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileUserViewState
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileViewModel
import com.pichurchyk.budgetsaver.ui.screen.themeselector.AppThemeSelector
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
    openAddCategory: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    val userViewState by viewModel.userViewState.collectAsState()
    val categoriesViewState by viewModel.categoriesViewState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(ProfileIntent.InitLoad)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
            .padding(WindowInsets.ime.asPaddingValues())
    ) {
        Content(
            profileViewState = userViewState,
            categoriesViewState = categoriesViewState,
            callViewModel = { viewModel.handleIntent(it) },
            onAddCategoryClick = openAddCategory
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    profileViewState: ProfileUserViewState,
    categoriesViewState: ProfileCategoriesViewState,
    callViewModel: (ProfileIntent) -> Unit,
    onAddCategoryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(44.dp)
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

        AppThemeSelector(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}
