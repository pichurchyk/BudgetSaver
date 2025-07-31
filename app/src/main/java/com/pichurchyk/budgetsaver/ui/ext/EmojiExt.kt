package com.pichurchyk.budgetsaver.ui.ext

import net.fellbaum.jemoji.EmojiGroup

fun EmojiGroup.getEmoji(): String {
    return when (this) {
        EmojiGroup.SMILEYS_AND_EMOTION -> "\uD83D\uDE00"
        EmojiGroup.PEOPLE_AND_BODY -> "\uD83E\uDDD1"
        EmojiGroup.COMPONENT -> "C"
        EmojiGroup.ANIMALS_AND_NATURE -> "\uD83D\uDC28"
        EmojiGroup.FOOD_AND_DRINK -> "\uD83C\uDF54"
        EmojiGroup.TRAVEL_AND_PLACES -> "✈\uFE0F"
        EmojiGroup.ACTIVITIES -> "⚽"
        EmojiGroup.OBJECTS -> "O"
        EmojiGroup.SYMBOLS -> "S"
        EmojiGroup.FLAGS -> "\uD83D\uDEA9"
    }
}