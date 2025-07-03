package com.pichurchyk.budgetsaver.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.ui.common.CommonButton
import com.pichurchyk.budgetsaver.ui.common.Loader
import com.pichurchyk.budgetsaver.ui.screen.auth.viewmodel.AuthIntent
import com.pichurchyk.budgetsaver.ui.screen.auth.viewmodel.AuthViewModel
import com.pichurchyk.budgetsaver.ui.screen.auth.viewmodel.AuthViewState
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import com.pichurchyk.budgetsaver.R
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = koinViewModel(),
    openDashboard: () -> Unit
) {
    val viewState by viewModel.state.collectAsState()

    Content(
        viewState = viewState,
        callViewModel = { viewModel.handleIntent(it) },
        openDashboard = openDashboard
    )
}

@Composable
private fun Content(
    viewState: AuthViewState,
    callViewModel: (AuthIntent) -> Unit,
    openDashboard: () -> Unit
) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    val googleAuthClient by remember {
        mutableStateOf(GoogleAuthClient(context))
    }

    val accountGoogleIdToken by googleAuthClient.accountGoogleIdToken.collectAsState()

    LaunchedEffect(accountGoogleIdToken) {
        accountGoogleIdToken?.let { account ->
            callViewModel.invoke(AuthIntent.Auth(account))
        }
    }

    LaunchedEffect(Unit) {
        callViewModel.invoke(AuthIntent.CheckSignedInUser)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(R.string.manage_your_finances_with_ease),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        when (viewState) {
            is AuthViewState.Success.NotSignedIn -> {
                CommonButton(
                    modifier = Modifier.padding(top = 26.dp),
                    value = stringResource(R.string.sign_in_with_google),
                    onClick = {
                        coroutineScope.launch {
                            googleAuthClient.startSignIn()
                        }
                    }
                )
            }

            is AuthViewState.Success.SignedIn -> {
                LaunchedEffect(Unit) {
                    openDashboard()
                }
            }

            is AuthViewState.Loading -> {
                Loader(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(top = 26.dp),
                )
            }

            is AuthViewState.Error -> {}
            is AuthViewState.Init -> {}
        }
    }
}

@Composable
@Preview
private fun Preview() {
    AppTheme() {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Content(
                viewState = AuthViewState.Loading,
                callViewModel = {},
                openDashboard = {}
            )
        }
    }
}