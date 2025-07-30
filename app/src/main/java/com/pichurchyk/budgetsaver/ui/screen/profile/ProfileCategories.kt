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
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.ui.common.CommonButton
import com.pichurchyk.budgetsaver.ui.common.CommonInput
import com.pichurchyk.budgetsaver.ui.common.PreviewMocks
import com.pichurchyk.budgetsaver.ui.common.category.TransactionCategoryChip
import com.pichurchyk.budgetsaver.ui.common.category.TransactionCategoryChipPlaceHolder
import com.pichurchyk.budgetsaver.ui.ext.asErrorMessage
import com.pichurchyk.budgetsaver.ui.ext.shimmerBackground
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileCategoriesUiStatus
import com.pichurchyk.budgetsaver.ui.screen.profile.viewmodel.ProfileCategoriesViewState
import com.pichurchyk.budgetsaver.ui.theme.AppTheme

@Composable
fun ProfileCategories(
    modifier: Modifier,
    viewState: ProfileCategoriesViewState,
    onSearchValueChanged: (String) -> Unit,
    onChipClicked: (TransactionCategory) -> Unit,
    onDeleteChipClick: (TransactionCategory) -> Unit,
    onAddCategoryClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
            text = stringResource(R.string.categories),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewState.categories.isNotEmpty()) {
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
            }

            CategoriesGrid(
                viewState = viewState,
                onChipClicked = onChipClicked,
                onDeleteChipClick = onDeleteChipClick,
                onAddCategoryClick = onAddCategoryClick
            )
        }
    }
}

@Composable
private fun CategoriesGrid(
    viewState: ProfileCategoriesViewState,
    onChipClicked: (TransactionCategory) -> Unit,
    onDeleteChipClick: (TransactionCategory) -> Unit,
    onAddCategoryClick: () -> Unit
) {
    val itemCount = when (viewState.status) {
        is ProfileCategoriesUiStatus.Idle, is ProfileCategoriesUiStatus.Error -> viewState.filteredCategories.size
        is ProfileCategoriesUiStatus.Loading -> 30
    }

    val rows = when {
        itemCount == 0 -> 0
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
            .fillMaxWidth()
    ) {

        if (viewState.categories.isEmpty() && viewState.status == ProfileCategoriesUiStatus.Idle) {
            NoCategories(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp)
            )
        }

        if (viewState.filteredCategories.isEmpty() && viewState.status == ProfileCategoriesUiStatus.Idle && viewState.search.isNotEmpty()) {
            NothingFound(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 16.dp)
            )
        }

        if (rows != 0) {
            if (viewState.status != ProfileCategoriesUiStatus.Loading) {
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
                                    onItemLongClick = { onDeleteChipClick(it) })
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

        CommonButton(
            modifier = Modifier
                .padding(
                    end = 16.dp,
                    top = if (viewState.status is ProfileCategoriesUiStatus.Error) 0.dp else 20.dp
                )
                .align(Alignment.End),
            value = stringResource(R.string.add_category),
            onClick = onAddCategoryClick
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
private fun NoCategories(
    modifier: Modifier
) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.you_have_no_categories),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    AppTheme {
        ProfileCategories(
            modifier = Modifier,
            viewState = ProfileCategoriesViewState(
                status = ProfileCategoriesUiStatus.Idle,
                categories = PreviewMocks.categories,
                search = ""
            ),
            onChipClicked = {},
            onDeleteChipClick = {},
            onSearchValueChanged = {},
            onAddCategoryClick = {})
    }
}