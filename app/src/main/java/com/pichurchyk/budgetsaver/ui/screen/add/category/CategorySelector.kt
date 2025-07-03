package com.pichurchyk.budgetsaver.ui.screen.add.category

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.ui.common.TransactionCategoryChip
import com.pichurchyk.budgetsaver.ui.common.CommonInput
import com.pichurchyk.budgetsaver.ui.common.Loader
import com.pichurchyk.budgetsaver.ui.screen.add.category.viewmodel.CategorySelectorIntent
import com.pichurchyk.budgetsaver.ui.screen.add.category.viewmodel.CategorySelectorViewModel
import com.pichurchyk.budgetsaver.ui.screen.add.category.viewmodel.CategorySelectorViewState
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

                    LazyRow(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .height(66.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        if (isMultiSelect) {
                            item {
                                Box(modifier = Modifier.padding(end = 8.dp)) {
                                    val isAllSelected =
                                        state.selected.size == state.filteredBySearchCategories.size
                                    SelectAllChip(
                                        modifier = Modifier.padding(top = 26.dp),
                                        isAllSelected = isAllSelected,
                                        onClick = {
                                            viewModel.handleIntent(CategorySelectorIntent.ToggleAllCategories)

                                            onValuesSelected.invoke(state.selected)
                                        }
                                    )
                                }
                            }
                        }

                        if (state.selected.isNotEmpty()) {
                            item(key = "selected_block") {
                                Column(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .animateItem()
                                ) {
                                    Text(
                                        text = stringResource(R.string.selected),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .animateContentSize(animationSpec = tween(durationMillis = 300))
                                            .background(
                                                MaterialTheme.colorScheme.primary.copy(0.05f),
                                                RoundedCornerShape(16.dp)
                                            )
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.primary.copy(0.4f),
                                                RoundedCornerShape(16.dp)
                                            )
                                            .padding(8.dp)
                                    ) {
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            state.selected.forEach { category ->
                                                TransactionCategoryChip(
                                                    modifier = Modifier,
                                                    category = category,
                                                    isSelected = true,
                                                    onItemClick = {
                                                        viewModel.handleIntent(
                                                            CategorySelectorIntent.ToggleCategory(it)
                                                        )

                                                        onValuesSelected.invoke(listOf(it))
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        items(
                            state.unselected,
                            key = { it.title }
                        ) { category ->
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .animateItem()
                            ) {
                                TransactionCategoryChip(
                                    modifier = Modifier.padding(top = 26.dp),
                                    category = category,
                                    isSelected = false,
                                    onItemClick = {
                                        viewModel.handleIntent(
                                            CategorySelectorIntent.ToggleCategory(it)
                                        )

                                        onValuesSelected.invoke(listOf(it))
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
