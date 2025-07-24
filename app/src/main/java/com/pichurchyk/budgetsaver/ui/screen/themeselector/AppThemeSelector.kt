package com.pichurchyk.budgetsaver.ui.screen.themeselector

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.ui.common.Loader
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileViewModel
import com.pichurchyk.budgetsaver.ui.screen.themeselector.viewmodel.AppThemeSelectorIntent
import com.pichurchyk.budgetsaver.ui.screen.themeselector.viewmodel.AppThemeSelectorViewModel
import com.pichurchyk.budgetsaver.ui.screen.themeselector.viewmodel.AppThemeSelectorViewState
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppThemeSelector(
    modifier: Modifier = Modifier,
    viewModel: AppThemeSelectorViewModel = koinViewModel(),
) {
    val viewState by viewModel.viewState.collectAsState()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
            text = stringResource(R.string.theme),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        when (val state = viewState) {
            is AppThemeSelectorViewState.Loading -> {
                Loader()
            }

            is AppThemeSelectorViewState.Idle -> {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppThemeOption.entries.forEach { option ->
                        AppThemeSelectorChip(
                            modifier = Modifier,
                            option = option,
                            isSelected = option == state.selectedOption
                        ) {
                            viewModel.handleIntent(AppThemeSelectorIntent.Select(option))
                        }
                    }
                }
            }
        }
    }
}