package com.pichurchyk.budgetsaver.ui.screen.transaction

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionType
import com.pichurchyk.budgetsaver.ui.ext.getColor
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import com.pichurchyk.budgetsaver.ui.theme.appFont
import com.pichurchyk.budgetsaver.ui.theme.disableGrey

@Composable
fun TransactionValueInput(
    modifier: Modifier = Modifier,
    value: String? = null,
    enabled: Boolean = true,
    isReadOnly: Boolean = false,
    error: Boolean = false,
    transactionType: TransactionType,
    onValueChanged: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .padding(2.dp),
        value = value ?: "",
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(0f),
            focusedContainerColor = MaterialTheme.colorScheme.primary.copy(0f),
            unfocusedTextColor = transactionType.getColor(),
            focusedTextColor = transactionType.getColor(),
            cursorColor = transactionType.getColor(),
            errorTextColor = MaterialTheme.colorScheme.onErrorContainer,
            errorContainerColor = MaterialTheme.colorScheme.errorContainer,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(0f),
            disabledPlaceholderColor = disableGrey.copy(0.2f),
            focusedPlaceholderColor = disableGrey.copy(0.2f),
            unfocusedPlaceholderColor = disableGrey.copy(0.2f),
            errorPlaceholderColor = disableGrey.copy(0.2f),
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(0f),
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(0f),
            disabledBorderColor = MaterialTheme.colorScheme.primary.copy(0f)
        ),
        enabled = enabled,
        isError = error,
        readOnly = isReadOnly,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        placeholder = {
            Text(
                text = stringResource(R.string.amount),
                style = TextStyle(
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = appFont
                )
            )
        },
        textStyle = TextStyle(
            fontSize = 46.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = appFont
        ),
        onValueChange = onValueChanged
    )
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    AppTheme {
        TransactionValueInput(
            modifier = Modifier,
            value = "",
            enabled = true,
            error = false,
            transactionType = TransactionType.INCOMES
        ) { }
    }
}