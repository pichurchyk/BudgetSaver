package com.pichurchyk.budgetsaver.ui.ext

import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    fun toStringWithPattern(
        dateInstant: Instant,
        pattern: String,
        timeZone: TimeZone = TimeZone.currentSystemDefault()
    ): String {
        val zoneId = java.time.ZoneId.of(timeZone.id)
        val zonedDateTime = java.time.ZonedDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(dateInstant.toEpochMilliseconds()),
            zoneId
        )
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return formatter.format(zonedDateTime)
    }


    fun Instant.toTheEndOfDay(timeZone: TimeZone): Instant {
        val localDate = this.toLocalDateTime(timeZone).date
        val endOfDay = LocalDateTime(
            year = localDate.year,
            monthNumber = localDate.monthNumber,
            dayOfMonth = localDate.dayOfMonth,
            hour = 23,
            minute = 59,
            second = 59,
            nanosecond = 999_999_999
        )
        return endOfDay.toInstant(timeZone)
    }
    fun Instant.toTheStartOfDay(timeZone: TimeZone = TimeZone.currentSystemDefault()): Instant {
        val localDate = this.toLocalDateTime(timeZone).date
        return localDate.atStartOfDayIn(timeZone)
    }

    fun TransactionDate.asStartOfTheDay(): TransactionDate {
        val startInstant = dateInstant.toTheStartOfDay(timeZone)
        return TransactionDate(startInstant, timeZone)
    }


    fun TransactionDate.asEndOfTheDay(): TransactionDate {
        val endInstant = dateInstant.toTheEndOfDay(timeZone)
        return TransactionDate(endInstant, timeZone)
    }
}