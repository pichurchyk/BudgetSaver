package com.pichurchyk.budgetsaver.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.ui.theme.AppTheme

@Composable
fun CommonInput(
    modifier: Modifier = Modifier,
    value: String? = null,
    placeholder: String? = null,
    headline: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true,
    isReadOnly: Boolean = false,
    isOptional: Boolean = true,
    height: Dp = 50.dp,
    error: Boolean = false,
    onValueChanged: (String) -> Unit
) {
    Column(modifier) {
        headline?.let {
            Row {
                Text(
                    modifier = Modifier.padding(start = 18.dp),
                    text = it,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelSmall,
                )

                if (!isOptional) {
                    Text(
                        modifier = Modifier,
                        text = "*",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(12.dp))
                .padding(2.dp),
            value = value ?: "",
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(0.05f),
                focusedContainerColor = MaterialTheme.colorScheme.primary.copy(0.05f),
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.primary,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(0.4f),
                unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(0.4f),
                errorTextColor = MaterialTheme.colorScheme.onErrorContainer,
                errorContainerColor = MaterialTheme.colorScheme.errorContainer,
                errorIndicatorColor = MaterialTheme.colorScheme.errorContainer
            ),
            enabled = enabled,
            isError = error,
            readOnly = isReadOnly,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            placeholder = {
                placeholder?.let {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                        text = it,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            },
            textStyle = MaterialTheme.typography.labelSmall,
            onValueChange = onValueChanged
        )
    }
}

@Composable
@Preview
private fun Preview() {
    AppTheme {
        CommonInput(
            modifier = Modifier,
            value = null,
            enabled = false,
            error = true,
            placeholder = "Placeholder",
            headline = "Headline"
        ) { }
    }
}