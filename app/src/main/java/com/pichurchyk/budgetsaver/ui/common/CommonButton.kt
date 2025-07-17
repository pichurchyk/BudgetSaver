package com.pichurchyk.budgetsaver.ui.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.ui.theme.AppTheme

@Composable
fun CommonButton(
    modifier: Modifier = Modifier,
    value: String,
    colors: ButtonColors = ButtonDefaults.buttonColors().copy(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(0.4f),
        disabledContentColor = MaterialTheme.colorScheme.onPrimary
    ),
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier.clip(RoundedCornerShape(100.dp)),
        onClick = onClick,
        colors = colors
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
@Preview
private fun Preview() {
    AppTheme {
        CommonButton(
            modifier = Modifier,
            value = "Click",
        ) { }
    }
}