package com.pichurchyk.budgetsaver.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.ui.theme.notificationGreenDark
import com.pichurchyk.budgetsaver.ui.theme.notificationGreenLight
import com.pichurchyk.budgetsaver.ui.theme.notificationRedDark
import com.pichurchyk.budgetsaver.ui.theme.notificationRedLight

@Composable
fun TwoOptionsSelector(
    modifier: Modifier = Modifier,
    title: String,
    positiveText: String,
    negativeText: String,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )

        Row(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            CommonButton(
                modifier = Modifier.weight(1f),
                value = negativeText,
                onClick = onNegativeClick,
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = notificationGreenLight,
                    contentColor = notificationGreenDark
                )
            )

            CommonButton(
                modifier = Modifier.weight(1f),
                value = positiveText,
                onClick = onPositiveClick,
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = notificationRedLight,
                    contentColor = notificationRedDark
                )
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    TwoOptionsSelector(
        modifier = Modifier,
        title = stringResource(R.string.delete),
        positiveText = stringResource(R.string.yes),
        negativeText = stringResource(R.string.no),
        onPositiveClick = {},
        onNegativeClick = {},
    )
}