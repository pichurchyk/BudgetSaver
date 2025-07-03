package com.pichurchyk.budgetsaver.ui.common.header

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector

sealed class HeaderIcon(
    open val onClick: () -> Unit,
    open val contentDescription: String? = ""
) {
    data class DrawableIcon(
        @DrawableRes val iconRes: Int,
        override val onClick: () -> Unit,
        override val contentDescription: String? = ""
    ) : HeaderIcon(onClick, contentDescription)

    data class VectorIcon(
        val icon: ImageVector,
        override val onClick: () -> Unit,
        override val contentDescription: String? = ""
    ) : HeaderIcon(onClick, contentDescription)
}