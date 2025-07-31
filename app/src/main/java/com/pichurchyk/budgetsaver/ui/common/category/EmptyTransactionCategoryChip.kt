package com.pichurchyk.budgetsaver.ui.common.category


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.ui.ext.fromHex
import com.pichurchyk.budgetsaver.ui.ext.toHex
import com.pichurchyk.budgetsaver.ui.theme.disableGrey


@Composable
fun EmptyTransactionCategoryChip(
    modifier: Modifier,
    isSelected: Boolean,
    onItemClick: () -> Unit,
    onItemLongClick: () -> Unit = {}
) {
    val bgColor = if (isSelected) disableGrey.copy(0f) else disableGrey.copy(0.1f)
    val textColor = MaterialTheme.colorScheme.onBackground

    Text(
        modifier = modifier
            .clip(RoundedCornerShape(100))
            .background(bgColor)
            .border(1.dp, textColor.copy(0.6f), RoundedCornerShape(100))
            .combinedClickable(
                onClick = { onItemClick() },
                onLongClick = { onItemLongClick() }
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        text = "❓   ${stringResource(R.string.no_category)}",
        style = MaterialTheme.typography.labelMedium,
        color = textColor
    )
}