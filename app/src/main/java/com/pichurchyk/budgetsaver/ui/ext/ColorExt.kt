package com.pichurchyk.budgetsaver.ui.ext

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import java.util.Locale

fun Color.Companion.fromHex(colorString: String): Color {
    val formatted = if (colorString.startsWith("#")) colorString else "#$colorString"
    return Color(formatted.toColorInt())
}


fun Color.toHex(withAlpha: Boolean = false): String {
    val argb = this.toArgb()
    return if (withAlpha) {
        String.format(Locale.US, "#%08X", argb)
    } else {
        String.format(Locale.US, "#%06X", 0xFFFFFF and argb)
    }
}