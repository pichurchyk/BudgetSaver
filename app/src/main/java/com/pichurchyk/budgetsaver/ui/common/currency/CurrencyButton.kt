package com.pichurchyk.budgetsaver.ui.common.currency

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import com.pichurchyk.budgetsaver.ui.theme.appFont

@Composable
fun CurrencyButton(
    modifier: Modifier,
    value: String
) {
    Text(
        modifier = modifier,
        text = value,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary.copy(0.5f),
        fontSize = 46.sp,
        style = TextStyle(
            fontSize = 46.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = appFont,
            letterSpacing = -(2).sp
        ),
    )
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