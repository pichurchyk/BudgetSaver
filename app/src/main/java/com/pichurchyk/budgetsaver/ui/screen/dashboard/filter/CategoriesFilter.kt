package com.pichurchyk.budgetsaver.ui.screen.dashboard.filter

import androidx.compose.animation.core.tween
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.ui.common.category.EmptyTransactionCategoryChip
import com.pichurchyk.budgetsaver.ui.common.category.TransactionCategoryChip
import com.pichurchyk.budgetsaver.ui.ext.doOnClick
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import com.pichurchyk.budgetsaver.ui.theme.disableGrey

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun CategoriesFilter(
    modifier: Modifier = Modifier,
    allCategories: List<TransactionCategory?>,
    selectedItems: List<TransactionCategory?>,
    onItemClick: (TransactionCategory?) -> Unit,
    onSelectAllClick: () -> Unit
) {
    val (selectedCategories, unselectedCategories) = remember(allCategories, selectedItems) {
        allCategories.partition { selectedItems.contains(it) }
    }

    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp),
            text = stringResource(R.string.categories),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyRow(
            modifier = Modifier
                .padding(top = 4.dp)
                .height(66.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {

            item {
                Box(modifier = Modifier.padding(end = 8.dp)) {
                    val isAllSelected = selectedItems.size == allCategories.size
                    SelectAllChip(
                        modifier = Modifier.padding(top = 26.dp),
                        isAllSelected = isAllSelected,
                        onClick = onSelectAllClick
                    )
                }
            }

            if (selectedCategories.isNotEmpty()) {
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
                                selectedCategories.forEach { category ->
                                    if (category != null) {
                                        TransactionCategoryChip(
                                            modifier = Modifier,
                                            category = category,
                                            isSelected = true,
                                            onItemClick = onItemClick
                                        )
                                    } else {
                                        EmptyTransactionCategoryChip(
                                            modifier = Modifier,
                                            isSelected = true,
                                            onItemClick = { onItemClick(null) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            items(
                unselectedCategories,
                key = { it?.title ?: "" }
            ) { category ->
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .animateItem()
                ) {
                    if (category != null) {
                        TransactionCategoryChip(
                            modifier = Modifier.padding(top = 26.dp),
                            category = category,
                            isSelected = false,
                            onItemClick = onItemClick
                        )
                    } else {
                        EmptyTransactionCategoryChip(
                            modifier = Modifier.padding(top = 26.dp),
                            isSelected = false,
                            onItemClick = { onItemClick(null) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun SelectAllChip(
    modifier: Modifier,
    isAllSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isAllSelected) {
        MaterialTheme.colorScheme.primary.copy(0.1f)
    } else {
        disableGrey.copy(0.1f)
    }

    val textColor = if (isAllSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    Text(
        modifier = modifier
            .clip(RoundedCornerShape(100))
            .background(bgColor)
            .border(1.dp, textColor.copy(0.6f), RoundedCornerShape(100))
            .doOnClick(onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        text = stringResource(R.string.all),
        style = MaterialTheme.typography.labelMedium,
        color = textColor
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewCategoriesFilter() {
    val categories = listOf(
        TransactionCategory("Food", "🍔", "#FF7043"),
        TransactionCategory("Transport", "🚌", "#42A5F5"),
        TransactionCategory("Health", "❤️", "#EC407A"),
        TransactionCategory("Gifts", "🎁", "#66BB6A"),
        TransactionCategory("Entertainment", "🎮", "#AB47BC"),
    )

    AppTheme {
        Box(Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 16.dp)) {
            CategoriesFilter(
                allCategories = categories,
                selectedItems = categories.take(2), // Preview with 2 items selected
                onItemClick = {},
                onSelectAllClick = {}
            )
        }
    }
}