package com.pichurchyk.budgetsaver.ui.common.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.ui.ext.doOnClick

@Composable
fun AppNotification(
    modifier: Modifier,
    message: String,
    action: (() -> Unit)? = {},
    actionText: String? = null,
    type: NotificationType
) {

    ElevatedCard(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = type.bgColor
        )
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = message,
                    color = type.textColor,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                )

                actionText?.let {
                    Text(
                        maxLines = 1,
                        modifier = Modifier.doOnClick { action?.invoke() },
                        text = actionText,
                        color = type.textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    Box(
        Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        AppNotification(
            modifier = Modifier,
            message = "Message das das das dsa das dsa das das das da",
            action = {},
            actionText = "Action",
            type = NotificationType.ERROR
        )
    }
}