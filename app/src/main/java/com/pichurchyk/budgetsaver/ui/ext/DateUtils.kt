package com.pichurchyk.budgetsaver.ui.ext

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
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

    fun Instant.toTheEndOfDay(timeZone: TimeZone = TimeZone.currentSystemDefault()): Instant {
        val localDate = this.toLocalDateTime(timeZone).date
        return localDate
            .plus(1, DateTimeUnit.DAY)
            .atStartOfDayIn(timeZone)
            .minus(1, DateTimeUnit.MILLISECOND)
    }}