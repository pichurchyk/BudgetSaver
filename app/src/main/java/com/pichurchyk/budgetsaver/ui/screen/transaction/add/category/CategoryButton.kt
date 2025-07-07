package com.pichurchyk.budgetsaver.ui.screen.transaction.add.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.ui.ext.fromHex
import com.pichurchyk.budgetsaver.ui.ext.toHex
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import com.pichurchyk.budgetsaver.ui.theme.disableGrey

@Composable
fun CategoryButton(
    modifier: Modifier,
    value: TransactionCategory?,
    error: Boolean = false
) {
    val errorColor = MaterialTheme.colorScheme.errorContainer
    val errorTextColor = MaterialTheme.colorScheme.onErrorContainer

    val categoryColor = Color.fromHex(value?.color ?: MaterialTheme.colorScheme.primary.toHex())

    val bgColor = if (!error) {
        if (value != null) categoryColor.copy(0.1f) else disableGrey.copy(0.1f)
    } else {
        errorColor
    }

    val textColor = if (!error) {
        if (value != null) categoryColor else MaterialTheme.colorScheme.onBackground
    } else {
        errorTextColor
    }

    Box(
        modifier = modifier
            .height(46.dp)
            .background(bgColor, RoundedCornerShape(10))
            .border(1.dp, bgColor.copy(0.6f), RoundedCornerShape(10))
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier,
            text = value?.asPrettyText ?: stringResource(R.string.category),
            textAlign = TextAlign.Center,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}