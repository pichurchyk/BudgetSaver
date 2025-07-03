package com.pichurchyk.budgetsaver.ui.screen.dashboard.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionSubCategory

@Composable
private fun TransactionSubCategory(category: TransactionSubCategory) {
    Text(
        modifier = Modifier
            .background(
                Color(category.color.toColorInt()).copy(0.2f),
                RoundedCornerShape(100)
            )
            .padding(horizontal = 4.dp, vertical = 2.dp),
        text = category.title,
        style = MaterialTheme.typography.bodySmall,
        color = Color(category.color.toColorInt())
    )
}