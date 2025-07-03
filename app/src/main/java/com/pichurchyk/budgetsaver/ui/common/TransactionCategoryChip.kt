package com.pichurchyk.budgetsaver.ui.common


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.ui.ext.doOnClick
import com.pichurchyk.budgetsaver.ui.ext.fromHex
import com.pichurchyk.budgetsaver.ui.ext.toHex
import com.pichurchyk.budgetsaver.ui.theme.disableGrey


@Composable
fun TransactionCategoryChip(
    modifier: Modifier,
    category: TransactionCategory,
    isSelected: Boolean,
    onItemClick: (TransactionCategory) -> Unit
) {
    val categoryColor = Color.fromHex(category.color ?: MaterialTheme.colorScheme.primary.toHex())
    val bgColor = if (isSelected) categoryColor.copy(0.1f) else disableGrey.copy(0.1f)
    val textColor = if (isSelected) categoryColor else MaterialTheme.colorScheme.onBackground

    Text(
        modifier = modifier
            .clip(RoundedCornerShape(100))
            .background(bgColor)
            .border(1.dp, textColor.copy(0.6f), RoundedCornerShape(100))
            .doOnClick { onItemClick(category) }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        text = category.asPrettyText,
        style = MaterialTheme.typography.labelMedium,
        color = textColor
    )
}