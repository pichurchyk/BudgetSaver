package com.pichurchyk.budgetsaver.ui.screen.profile

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationAction
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationController
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationEvent
import com.pichurchyk.budgetsaver.ui.common.notification.NotificationType
import com.pichurchyk.budgetsaver.ui.ext.asErrorMessage
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileIntent
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileUiStatus
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileUserViewState
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileViewModel
import com.pichurchyk.budgetsaver.ui.screen.themeselector.AppThemeSelector
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val focusManager = LocalFocusManager.current

    val viewState by viewModel.viewState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewState.status) {
        when (val uiStatus = viewState.status) {
            is ProfileUiStatus.Error -> {
                NotificationController.sendEvent(
                    NotificationEvent(
                        message = context.getString(uiStatus.error.asErrorMessage()),
                        type = NotificationType.ERROR,
                        action = NotificationAction(
                            name = context.getString(R.string.retry),
                            action = {
                                uiStatus.lastAction.invoke()

                                viewModel.handleIntent(ProfileIntent.DismissNotification)
                            }
                        )
                    )
                )
            }

            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Content(
            profileViewState = viewState,
            callViewModel = { viewModel.handleIntent(it) })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    profileViewState: ProfileUserViewState,
    callViewModel: (ProfileIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxSize()
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileCard(
            modifier = Modifier,
            viewState = profileViewState
        )

        AppThemeSelector(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
    }
}



@Preview
@Composable
private fun Preview() {
    AppTheme {
        Content(
            profileViewState = ProfileUserViewState(),
            callViewModel = {},
        )
    }
}