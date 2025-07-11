package com.pichurchyk.budgetsaver.domain.model.transaction

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import java.time.format.DateTimeFormatter

@Serializable
data class TransactionDate(
    val dateInstant: Instant,
    val timeZone: TimeZone
) {
    fun toStringWithPattern(pattern: String): String {
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