package com.pichurchyk.budgetsaver.ui.screen.themeselector

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.ui.ext.doOnClick
import com.pichurchyk.budgetsaver.ui.ext.getTitle
import com.pichurchyk.budgetsaver.ui.theme.disableGrey

enum class AppThemeOption() {
    DARK, LIGHT, SYSTEM
}

@Composable
fun AppThemeSelectorChip(
    modifier: Modifier,
    option: AppThemeOption,
    isSelected: Boolean,
    onItemClick: (AppThemeOption) -> Unit
) {
    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else disableGrey

    Text(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(textColor.copy(0.1f))
            .border(1.dp, textColor.copy(0.6f), RoundedCornerShape(12.dp))
            .doOnClick { onItemClick(option) }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        text = option.getTitle(),
        style = MaterialTheme.typography.labelMedium,
        color = textColor
    )
}