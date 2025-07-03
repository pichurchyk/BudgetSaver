package com.pichurchyk.budgetsaver.domain.model.transaction

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class TransactionDate(
    val dateInstant: Instant,
    val zoneId: TimeZone
) {
    val asLocalDateTime: LocalDateTime get() = dateInstant.toLocalDateTime(zoneId)
}