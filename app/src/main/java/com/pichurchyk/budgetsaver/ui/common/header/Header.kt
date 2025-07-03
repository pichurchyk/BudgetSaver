package com.pichurchyk.budgetsaver.ui.common.header

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.ui.theme.AppTheme

@Composable
fun Header(
    modifier: Modifier = Modifier,
    value: String,
    startIcon: HeaderIcon? = null,
    endIcon: HeaderIcon? = null
) {
    Row(
        modifier = modifier.fillMaxWidth().height(40.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val textStartPadding = if (startIcon == null) 24.dp else 0.dp
        val textEndPadding = if (endIcon == null) 24.dp else 0.dp

        startIcon?.let {
            when (startIcon) {
                is HeaderIcon.VectorIcon -> {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { startIcon.onClick },
                        imageVector = startIcon.icon,
                        contentDescription = startIcon.contentDescription
                    )
                }

                is HeaderIcon.DrawableIcon -> {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { startIcon.onClick },
                        painter = painterResource(startIcon.iconRes),
                        contentDescription = startIcon.contentDescription
                    )
                }
            }

        }

        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = textStartPadding, end = textEndPadding),
            textAlign = TextAlign.Center,
            text = value,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall
        )

        endIcon?.let {
            when (endIcon) {
                is HeaderIcon.VectorIcon -> {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { endIcon.onClick },
                        imageVector = endIcon.icon,
                        contentDescription = endIcon.contentDescription
                    )
                }

                is HeaderIcon.DrawableIcon -> {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { endIcon.onClick },
                        painter = painterResource(endIcon.iconRes),
                        contentDescription = endIcon.contentDescription
                    )
                }
            }

        }
    }
}

@Composable
@Preview
private fun Preview() {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Header(
                value = "Header",
                startIcon = HeaderIcon.VectorIcon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    onClick = {},
                    ""
                ),
                endIcon = HeaderIcon.VectorIcon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    onClick = {},
                    ""
                ),
            )
        }
    }
}