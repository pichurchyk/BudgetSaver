package com.pichurchyk.budgetsaver.ui.screen.transaction.add.currency

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.ui.theme.AppTheme

@Composable
fun CurrencyButton(
    modifier: Modifier,
    value: String
) {
    Box(
        modifier = modifier
            .height(46.dp)
            .background(MaterialTheme.colorScheme.primary.copy(0.05f), RoundedCornerShape(10))
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(0.5f), RoundedCornerShape(10))
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier,
            text = value,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary.copy(0.5f),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
@Preview
private fun Preview() {
    AppTheme {
        Box(
            Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            CurrencyButton(
                modifier = Modifier,
                value = "USD"
            )
        }
    }
}