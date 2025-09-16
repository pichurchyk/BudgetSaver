package com.pichurchyk.budgetsaver.domain.model.transaction

import com.pichurchyk.budgetsaver.ui.ext.DateUtils
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDate(
    val dateInstant: Instant,
    val timeZone: TimeZone
) {
    fun toStringWithPattern(pattern: String): String {
        return DateUtils.toStringWithPattern(dateInstant, pattern, timeZone)
    }

    companion object {
        fun createWithDefaultTimeZone(dateInstant: Instant): TransactionDate =
            TransactionDate(dateInstant, TimeZone.currentSystemDefault())
    }
}