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
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.ui.common.CommonInput
import com.pichurchyk.budgetsaver.ui.common.category.TransactionCategoryChip
import com.pichurchyk.budgetsaver.ui.common.category.TransactionCategoryChipPlaceHolder
import com.pichurchyk.budgetsaver.ui.ext.asErrorMessage
import com.pichurchyk.budgetsaver.ui.ext.shimmerBackground
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileCategoriesUiStatus
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileCategoriesViewState

@Composable
fun ProfileCategories(
    modifier: Modifier,
    viewState: ProfileCategoriesViewState,
    onSearchValueChanged: (String) -> Unit,
    onChipClicked: (TransactionCategory) -> Unit,
    onDeleteChipClick: (TransactionCategory) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
            text = stringResource(R.string.categories),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CommonInput(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = stringResource(R.string.search),
                enabled = viewState.status !is ProfileCategoriesUiStatus.Error,
                value = viewState.search
            ) {
                onSearchValueChanged(it)
            }

            CategoriesGrid(
                viewState = viewState,
                onChipClicked = onChipClicked,
                onDeleteChipClick = onDeleteChipClick
            )
        }
    }
}

@Composable
private fun CategoriesGrid(
    viewState: ProfileCategoriesViewState,
    onChipClicked: (TransactionCategory) -> Unit,
    onDeleteChipClick: (TransactionCategory) -> Unit
) {
    val itemCount = when (viewState.status) {
        is ProfileCategoriesUiStatus.Idle, is ProfileCategoriesUiStatus.Error -> viewState.filteredCategories.size
        is ProfileCategoriesUiStatus.Loading -> 30
    }

    val rows = when {
        itemCount <= 8 -> 1
        itemCount <= 16 -> 2
        itemCount <= 32 -> 3
        else -> 4
    }

    val chipHeight = 38.dp
    val spacing = 4.dp

    val gridHeight = (chipHeight * rows) + (spacing * (rows - 2))

    Column(
        modifier = Modifier
            .padding(top = 20.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
            text = "*${stringResource(R.string.long_click_on_items_to_delete)}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

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
            userScrollEnabled = viewState.status !is ProfileCategoriesUiStatus.Loading
        ) {
            when (viewState.status) {
                is ProfileCategoriesUiStatus.Idle, is ProfileCategoriesUiStatus.Error -> {
                    items(viewState.filteredCategories) { item ->
                        Box {
                            TransactionCategoryChip(
                                modifier = Modifier,
                                category = item,
                                isSelected = false,
                                onItemClick = { onChipClicked(it) },
                                onItemLongClick = { onDeleteChipClick(it) }
                            )
                        }
                    }
                }

                is ProfileCategoriesUiStatus.Loading -> {
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

        if (viewState.status is ProfileCategoriesUiStatus.Error) {
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
    }
}