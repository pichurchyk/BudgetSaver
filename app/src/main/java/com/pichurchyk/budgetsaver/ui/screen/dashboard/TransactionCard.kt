package com.pichurchyk.budgetsaver.ui.screen.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.Money
import com.pichurchyk.budgetsaver.domain.model.transaction.Transaction
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionCategory
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionDate
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionSubCategory
import com.pichurchyk.budgetsaver.ui.common.TransactionCategoryChip
import com.pichurchyk.budgetsaver.ui.ext.doOnClick
import com.pichurchyk.budgetsaver.ui.ext.getColorBasedOnValue
import com.pichurchyk.budgetsaver.ui.ext.getTransactionDefaultTitle
import com.pichurchyk.budgetsaver.ui.ext.toMajorWithCurrency
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import java.math.BigInteger

@Composable
fun TransactionCard(
    modifier: Modifier = Modifier,
    transaction: Transaction,
    onEditClick: (transactionId: String) -> Unit
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }

    val titleTextSize =
        if (isExpanded) MaterialTheme.typography.headlineMedium.fontSize else MaterialTheme.typography.titleSmall.fontSize
    val valueTextSize =
        if (isExpanded) MaterialTheme.typography.headlineLarge.fontSize else MaterialTheme.typography.titleMedium.fontSize

    val animatedTitleTextSize by animateFloatAsState(
        targetValue = titleTextSize.value,
        animationSpec = tween(durationMillis = 200)
    )

    val animatedValueTextSize by animateFloatAsState(
        targetValue = valueTextSize.value,
        animationSpec = tween(durationMillis = 200)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                isExpanded = !isExpanded
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp, transaction.value.getColorBasedOnValue().copy(0.3f)),
    ) {
        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = {
                (fadeIn(animationSpec = tween(75)) +
                        scaleIn(initialScale = 0.92f, animationSpec = tween(75)))
                    .togetherWith(fadeOut(animationSpec = tween(90)))
            }
        ) { isExpanded ->
            if (isExpanded) {
                ExpandedCard(
                    transaction = transaction,
                    titleTextSize = animatedTitleTextSize,
                    valueTextSize = animatedValueTextSize,
                    onEditClick = onEditClick
                )
            } else {
                CollapsedCard(
                    transaction = transaction,
                    titleTextSize = animatedTitleTextSize,
                    valueTextSize = animatedValueTextSize
                )
            }
        }
    }
}

@Composable
private fun ExpandedCard(
    transaction: Transaction,
    titleTextSize: Float,
    valueTextSize: Float,
    onEditClick: (transactionId: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 20.dp)
            .animateContentSize(
                animationSpec = tween(durationMillis = 75)
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = transaction.title ?: transaction.getTransactionDefaultTitle(),
                style = MaterialTheme.typography.headlineMedium,
                fontSize = titleTextSize.sp
            )

            Icon(
                modifier = Modifier
                    .size(16.dp)
                    .doOnClick { onEditClick(transaction.uuid) },
                tint = MaterialTheme.colorScheme.onBackground.copy(0.6f),
                imageVector = Icons.Rounded.Edit,
                contentDescription = stringResource(R.string.edit)
            )
        }

        Text(
            modifier = Modifier.padding(),
            color = transaction.value.getColorBasedOnValue(),
            text = transaction.value.toMajorWithCurrency(),
            style = MaterialTheme.typography.headlineLarge,
            fontSize = valueTextSize.sp
        )

        Column {
            Text(
                modifier = Modifier.padding(),
                text = transaction.date.toStringWithPattern("MMM dd, yyyy HH:mm"),
                style = MaterialTheme.typography.titleMedium
            )

            TransactionCategoryChip(
                modifier = Modifier.padding(top = 10.dp),
                category = transaction.mainCategory,
                isSelected = false,
                onItemClick = {}
            )

            if (transaction.notes.isNotEmpty()) {
                Column {
                    DashedDivider(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                    )
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = transaction.notes,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun CollapsedCard(
    transaction: Transaction,
    titleTextSize: Float,
    valueTextSize: Float,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 20.dp)
            .animateContentSize(
                animationSpec = tween(durationMillis = 75)
            ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = transaction.mainCategory.emoji,
            style = MaterialTheme.typography.headlineMedium,
            fontSize = titleTextSize.sp
        )

        Text(
            modifier = Modifier.weight(1f),
            text = transaction.title ?: transaction.getTransactionDefaultTitle(),
            style = MaterialTheme.typography.headlineMedium,
            fontSize = titleTextSize.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            modifier = Modifier.weight(1f),
            color = transaction.value.getColorBasedOnValue(),
            text = transaction.value.toMajorWithCurrency(),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.End,
            fontSize = valueTextSize.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun DashedDivider(
    color: Color = MaterialTheme.colorScheme.onBackground.copy(0.2f),
    thickness: Dp = 1.dp,
    dashWidth: Dp = 10.dp,
    gapWidth: Dp = 10.dp,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val dashPx = with(density) { dashWidth.toPx() }
    val gapPx = with(density) { gapWidth.toPx() }
    val thicknessPx = with(density) { thickness.toPx() }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
    ) {
        var startX = 0f
        while (startX < size.width) {
            drawLine(
                color = color,
                start = Offset(x = startX, y = size.height / 2),
                end = Offset(x = startX + dashPx, y = size.height / 2),
                strokeWidth = thicknessPx
            )
            startX += dashPx + gapPx
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun Preview() {
    AppTheme {
        TransactionCard(
            modifier = Modifier,
            onEditClick = {},
            transaction = Transaction(
                uuid = "",
                title = "Bus ticket",
                value = Money(
                    amountMinor = BigInteger("0"),
                    currency = "USD"
                ),
                notes = "Grocery shopping at local market",
                date = TransactionDate(
                    dateInstant = Instant.fromEpochMilliseconds(
                        1748198228000
                    ),
                    timeZone = TimeZone.UTC
                ),
                mainCategory = TransactionCategory(
                    title = "Food",
                    emoji = "🍎",
                    color = "#C5C22B",
                    uuid = "123"
                ),
                subCategory = listOf(
                    TransactionSubCategory(
                        title = "Groceries",
                        color = "#F5C26B"
                    )
                )
            )
        )
    }
}