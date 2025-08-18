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
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.domain.model.category.TransactionCategory
import com.pichurchyk.budgetsaver.ui.ext.fromHex
import com.pichurchyk.budgetsaver.ui.ext.toHex
import com.pichurchyk.budgetsaver.ui.theme.disableGrey


@Composable
fun TransactionCategoryChip(
    modifier: Modifier,
    category: TransactionCategory,
    isSelected: Boolean,
    onItemClick: (TransactionCategory) -> Unit,
    onItemLongClick: (TransactionCategory) -> Unit = {}
) {
    val categoryColor = Color.fromHex(category.color ?: MaterialTheme.colorScheme.primary.toHex())
    val bgColor = if (isSelected) categoryColor.copy(0.1f) else disableGrey.copy(0.1f)
    val textColor = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground

    Text(
        modifier = modifier
            .clip(RoundedCornerShape(100))
            .background(bgColor)
            .border(1.dp, textColor.copy(0.6f), RoundedCornerShape(100))
            .combinedClickable(
                onClick = { onItemClick(category) },
                onLongClick = { onItemLongClick(category) }
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        text = category.asPrettyText,
        style = MaterialTheme.typography.labelMedium,
        color = textColor
    )
}