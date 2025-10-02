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
        val localDateTime = dateInstant.toLocalDateTime(timeZone)
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return formatter.format(
            java.time.LocalDateTime.of(
                localDateTime.year,
                localDateTime.monthNumber,
                localDateTime.dayOfMonth,
                localDateTime.hour,
                localDateTime.minute,
                localDateTime.second
            )
        )
    }

    fun Instant.toTheEndOfDay(timeZone: TimeZone = TimeZone.currentSystemDefault()): Instant {
        val localDate = this.toLocalDateTime(timeZone).date
        return localDate
            .plus(1, DateTimeUnit.DAY)
            .atStartOfDayIn(timeZone)
            .minus(1, DateTimeUnit.MILLISECOND)
    }

    fun Instant.toTheStartOfDay(timeZone: TimeZone = TimeZone.currentSystemDefault()): Instant {
        val localDate = this.toLocalDateTime(timeZone).date
        return localDate.atStartOfDayIn(timeZone)
    }

    fun TransactionDate.asStartOfTheDay(): TransactionDate {
        val localDate = dateInstant.toLocalDateTime(timeZone).date
        val startInstant = localDate.atStartOfDayIn(timeZone)
        return TransactionDate(startInstant, timeZone)
    }

    fun TransactionDate.asEndOfTheDay(): TransactionDate {
        val localDate = dateInstant.toLocalDateTime(timeZone).date
        val endOfDay = LocalDateTime(
            year = localDate.year,
            monthNumber = localDate.monthNumber,
            dayOfMonth = localDate.dayOfMonth,
            hour = 23,
            minute = 59,
            second = 59,
            nanosecond = 999_999_999
        )
        return TransactionDate(endOfDay.toInstant(timeZone), timeZone)
    }
}