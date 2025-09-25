package com.pichurchyk.budgetsaver.ui.screen.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionPreset
import com.pichurchyk.budgetsaver.ui.common.CommonButton
import com.pichurchyk.budgetsaver.ui.common.CommonInput
import com.pichurchyk.budgetsaver.ui.common.PreviewMocks
import com.pichurchyk.budgetsaver.ui.common.category.TransactionCategoryChipPlaceHolder
import com.pichurchyk.budgetsaver.ui.common.preset.TransactionPresetChip
import com.pichurchyk.budgetsaver.ui.ext.asErrorMessage
import com.pichurchyk.budgetsaver.ui.ext.shimmerBackground
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfilePresetsUiStatus
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfilePresetsViewState
import com.pichurchyk.budgetsaver.ui.theme.AppTheme

@Composable
fun ProfilePresets(
    modifier: Modifier,
    viewState: ProfilePresetsViewState,
    onSearchValueChanged: (String) -> Unit,
    onChipClicked: (TransactionPreset) -> Unit,
    onDeleteChipClick: (TransactionPreset) -> Unit,
    onAddPresetClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
            text = stringResource(R.string.presets),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewState.presets.isNotEmpty()) {
                CommonInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = stringResource(R.string.search),
                    enabled = viewState.status !is ProfilePresetsUiStatus.Error,
                    value = viewState.search
                ) {
                    onSearchValueChanged(it)
                }
            }

            PresetsGrid(
                viewState = viewState,
                onChipClicked = onChipClicked,
                onDeleteChipClick = onDeleteChipClick,
                onAddPresetClick = onAddPresetClick
            )
        }
    }
}

@Composable
private fun PresetsGrid(
    viewState: ProfilePresetsViewState,
    onChipClicked: (TransactionPreset) -> Unit,
    onDeleteChipClick: (TransactionPreset) -> Unit,
    onAddPresetClick: () -> Unit
) {
    val itemCount = when (viewState.status) {
        is ProfilePresetsUiStatus.Idle, is ProfilePresetsUiStatus.Error -> viewState.filteredPresets.size
        is ProfilePresetsUiStatus.Loading -> 30
    }

    val rows = when {
        itemCount == 0 -> 0
        itemCount <= 8 -> 1
        itemCount <= 16 -> 2
        itemCount <= 32 -> 3
        else -> 4
    }

    val chipHeight = 78.dp
    val spacing = 4.dp

    val gridHeight = (chipHeight * rows) + (spacing * (rows - 2))

    Column(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
    ) {

        if (viewState.presets.isEmpty() && viewState.status == ProfilePresetsUiStatus.Idle) {
            NoPresets(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp)
            )
        }

        if (viewState.filteredPresets.isEmpty() && viewState.status == ProfilePresetsUiStatus.Idle && viewState.search.isNotEmpty()) {
            NothingFound(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp)
            )
        }

        if (rows != 0) {
            if (viewState.status != ProfilePresetsUiStatus.Loading) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
                    text = "*${stringResource(R.string.long_click_on_items_to_delete)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            LazyHorizontalStaggeredGrid(
                rows = StaggeredGridCells.Fixed(rows),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(gridHeight),
                horizontalItemSpacing = 4.dp,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                ),
                userScrollEnabled = viewState.status !is ProfilePresetsUiStatus.Loading
            ) {
                when (viewState.status) {
                    is ProfilePresetsUiStatus.Idle, is ProfilePresetsUiStatus.Error -> {
                        items(viewState.filteredPresets) { item ->
                            Box {
                                TransactionPresetChip(
                                    modifier = Modifier,
                                    preset = item,
                                    isSelected = false,
                                    onItemClick = { onChipClicked(it) },
                                    onItemLongClick = { onDeleteChipClick(it) })
                            }
                        }
                    }

                    is ProfilePresetsUiStatus.Loading -> {
                        items(30) { index ->
                            Box {
                                TransactionCategoryChipPlaceHolder(
                                    Modifier.shimmerBackground(
                                        RoundedCornerShape(100)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        if (viewState.status is ProfilePresetsUiStatus.Error) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
                text = viewState.status.error.message
                    ?: stringResource(viewState.status.error.asErrorMessage()),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        CommonButton(
            modifier = Modifier
                .padding(
                    end = 16.dp,
                    top = if (viewState.status is ProfilePresetsUiStatus.Error) 0.dp else 20.dp
                )
                .align(Alignment.End),
            value = stringResource(R.string.add_preset),
            onClick = onAddPresetClick
        )
    }
}

@Composable
private fun NothingFound(
    modifier: Modifier
) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.nothing_found),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun NoPresets(
    modifier: Modifier
) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.you_have_no_presets),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    AppTheme {
        ProfilePresets(
            modifier = Modifier,
            viewState = ProfilePresetsViewState(
                status = ProfilePresetsUiStatus.Idle,
                presets = listOf(PreviewMocks.transactionPreset, PreviewMocks.transactionPreset),
                search = ""
            ),
            onChipClicked = {},
            onDeleteChipClick = {},
            onSearchValueChanged = {},
            onAddPresetClick = {})
    }
}