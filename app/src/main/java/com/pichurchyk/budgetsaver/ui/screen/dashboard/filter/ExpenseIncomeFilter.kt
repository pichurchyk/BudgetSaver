package com.pichurchyk.budgetsaver.ui.screen.dashboard.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import com.pichurchyk.budgetsaver.ui.ext.doOnClick
import com.pichurchyk.budgetsaver.ui.ext.getTitle
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import com.pichurchyk.budgetsaver.ui.theme.disableGrey
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.ui.common.TransactionTypeChip

@Composable
fun ExpenseIncomeFilter(
    modifier: Modifier = Modifier,
    selectedItems: List<TransactionType>,
    onSelectAllClick: () -> Unit,
    onItemClick: (TransactionType) -> Unit
) {
    val allItems by remember { mutableStateOf(TransactionType.entries) }

    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.type),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyRow(
            modifier = modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            item {
                val isSelected = selectedItems.containsAll(allItems)

                val bgColor = if (isSelected) {
                    MaterialTheme.colorScheme.primary.copy(0.1f)
                } else {
                    disableGrey.copy(0.1f)
                }

                val textColor = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onBackground
                }

                Text(
                    modifier = Modifier
                        .padding(2.dp)
                        .background(bgColor, RoundedCornerShape(100))
                        .border(1.dp, textColor.copy(0.6f), RoundedCornerShape(100))
                        .clip(RoundedCornerShape(100))
                        .clickable { onSelectAllClick() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    text = stringResource(R.string.all),
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor
                )
            }

            items(allItems) { filterItem ->
                val isSelected = selectedItems.contains(filterItem)

                TransactionTypeChip(
                    modifier = Modifier,
                    isSelected = isSelected,
                    value = filterItem,
                    onClick = { onItemClick(filterItem) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AppTheme {
        ExpenseIncomeFilter(modifier = Modifier, listOf(TransactionType.EXPENSES), {}, {})
    }
}