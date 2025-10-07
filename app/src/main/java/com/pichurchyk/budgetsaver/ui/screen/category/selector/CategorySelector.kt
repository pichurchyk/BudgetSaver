package com.pichurchyk.budgetsaver.ui.screen.category.selector

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.ui.common.CommonInput
import com.pichurchyk.budgetsaver.ui.common.Loader
import com.pichurchyk.budgetsaver.ui.common.category.TransactionCategoryChip
import com.pichurchyk.budgetsaver.ui.screen.category.viewmodel.CategorySelectorIntent
import com.pichurchyk.budgetsaver.ui.screen.category.viewmodel.CategorySelectorViewModel
import com.pichurchyk.budgetsaver.ui.screen.category.viewmodel.CategorySelectorViewState
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun CategorySelector(
    modifier: Modifier,
    selectedValues: List<TransactionCategory>,
    isMultiSelect: Boolean,
    isNullable: Boolean,
    onValuesSelected: (List<TransactionCategory>) -> Unit,
    viewModel: CategorySelectorViewModel = koinViewModel(
        key = "category_selector_${selectedValues.hashCode()}",
        parameters = { parametersOf(isMultiSelect, isNullable, selectedValues) }
    ),
) {
    val viewState by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(CategorySelectorIntent.LoadCategories)
    }

    Box(modifier = modifier) {
        when (val state = viewState) {
            is CategorySelectorViewState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Loader()
                }
            }

            is CategorySelectorViewState.Loaded -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CommonInput(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        placeholder = stringResource(R.string.search),
                        value = state.searchValue
                    ) {
                        viewModel.handleIntent(CategorySelectorIntent.ChangeSearchValue(it))
                    }

                    val allItems = if (isMultiSelect) {
                        state.filteredBySearchCategories.size + 1
                    } else {
                        state.filteredBySearchCategories.size
                    }

                    val itemCount = allItems
                    val rows = when {
                        itemCount == 0 -> 0
                        itemCount <= 8 -> 1
                        itemCount <= 16 -> 2
                        itemCount <= 32 -> 3
                        else -> 4
                    }

                    val chipHeight = 38.dp
                    val spacing = 4.dp
                    val gridHeight = (chipHeight * rows) + (spacing * (rows - 1))

                    if (rows != 0) {
                        LazyHorizontalStaggeredGrid(
                            rows = StaggeredGridCells.Fixed(rows),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(gridHeight)
                                .padding(top = 8.dp),
                            horizontalItemSpacing = 4.dp,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            if (isMultiSelect) {
                                item(key = "select_all") {
                                    val isAllSelected =
                                        state.selected.size == state.filteredBySearchCategories.size
                                    SelectAllChip(
                                        modifier = Modifier,
                                        isAllSelected = isAllSelected,
                                        onClick = {
                                            viewModel.handleIntent(CategorySelectorIntent.ToggleAllCategories)
                                            onValuesSelected.invoke(state.selected)
                                        }
                                    )
                                }
                            }

                            items(
                                state.selected,
                                key = { "selected_${it.uuid}" }
                            ) { category ->
                                TransactionCategoryChip(
                                    modifier = Modifier,
                                    category = category,
                                    isSelected = true,
                                    onItemClick = {
                                        viewModel.handleIntent(
                                            CategorySelectorIntent.ToggleCategory(
                                                it
                                            )
                                        )
                                        onValuesSelected.invoke(
                                            if (isMultiSelect) state.selected else listOf(it)
                                        )
                                    }
                                )
                            }

                            items(
                                state.unselected,
                                key = { "unselected_${it.uuid}" }
                            ) { category ->
                                TransactionCategoryChip(
                                    modifier = Modifier,
                                    category = category,
                                    isSelected = false,
                                    onItemClick = {
                                        viewModel.handleIntent(
                                            CategorySelectorIntent.ToggleCategory(
                                                it
                                            )
                                        )
                                        onValuesSelected.invoke(
                                            if (isMultiSelect) state.selected else listOf(it)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            is CategorySelectorViewState.Error -> {
                // Handle error UI
            }
        }
    }
}