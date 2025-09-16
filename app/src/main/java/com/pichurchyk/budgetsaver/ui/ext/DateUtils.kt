package com.pichurchyk.budgetsaver.ui.ext

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    fun toStringWithPattern(dateInstant: Instant, pattern: String, timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
        val localDateTime = dateInstant.toLocalDateTime(timeZone)
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return formatter.format(java.time.LocalDateTime.of(
            localDateTime.year,
            localDateTime.monthNumber,
            localDateTime.dayOfMonth,
            localDateTime.hour,
            localDateTime.minute,
            localDateTime.second
        ))
    }
}