package com.pichurchyk.budgetsaver.ui.screen.dashboard.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pichurchyk.budgetsaver.R
import com.pichurchyk.budgetsaver.domain.model.transaction.TransactionDate
import com.pichurchyk.budgetsaver.ui.common.PreviewMocks
import com.pichurchyk.budgetsaver.ui.ext.DateUtils
import com.pichurchyk.budgetsaver.ui.ext.DateUtils.toTheEndOfDay
import com.pichurchyk.budgetsaver.ui.ext.DateUtils.toTheStartOfDay
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@Composable
fun DashboardCalendarButton(
    modifier: Modifier = Modifier,
    dateRange: Pair<TransactionDate?, TransactionDate?>,
) {
    val start = dateRange.first
    val end = dateRange.second

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.period),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
        )

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val startText = start?.toStringWithPattern("MMM dd, yyyy") ?: stringResource(R.string.oldest)
            val endText = end?.toStringWithPattern("MMM dd, yyyy") ?: stringResource(R.string.newest)

            Text(
                text = startText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(" - ")
            Text(
                text = endText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardDateRangeCalendar(
    modifier: Modifier = Modifier,
    selectedDates: Pair<TransactionDate?, TransactionDate?>,
    onDatesSelected: (Pair<TransactionDate?, TransactionDate?>) -> Unit
) {
    val state = rememberDateRangePickerState(
        initialDisplayMode = DisplayMode.Picker,
        initialSelectedStartDateMillis = selectedDates.first?.let { start ->
            start.dateInstant
                .toLocalDateTime(start.timeZone)
                .date
                .atStartOfDayIn(TimeZone.UTC)
                .toEpochMilliseconds()
        },
        initialSelectedEndDateMillis = selectedDates.second?.let { end ->
            end.dateInstant
                .toLocalDateTime(end.timeZone)
                .date
                .atStartOfDayIn(TimeZone.UTC)
                .toEpochMilliseconds()
        },
    )

    DateRangePicker(
        modifier = modifier,
        showModeToggle = false,
        state = state,
        dateFormatter = DatePickerDefaults.dateFormatter(
            selectedDateSkeleton = "MM.dd.yyyy"
        ),
        colors = DatePickerDefaults.colors(
            containerColor = Color.Transparent,
            dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primary.copy(0.2f)
        ),
        title = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                textAlign = TextAlign.Center,
                text = stringResource(R.string.select_date_range),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        headline = {
            val zone = TimeZone.currentSystemDefault()

            val startDateTitle = state.selectedStartDateMillis?.let { millis ->
                val localDate = Instant.fromEpochMilliseconds(millis)
                    .toLocalDateTime(TimeZone.UTC)
                    .date
                val displayDate = localDate.atStartOfDayIn(zone).toLocalDateTime(zone).date
                DateUtils.toStringWithPattern(displayDate.atStartOfDayIn(zone), "MMM dd, yyyy", zone)
            } ?: stringResource(R.string.select_date)

            val endDateTitle = state.selectedEndDateMillis?.let { millis ->
                val localDate = Instant.fromEpochMilliseconds(millis)
                    .toLocalDateTime(TimeZone.UTC)
                    .date
                val displayDate = localDate.atStartOfDayIn(zone).toLocalDateTime(zone).date
                DateUtils.toStringWithPattern(displayDate.atStartOfDayIn(zone), "MMM dd, yyyy", zone)
            } ?: stringResource(R.string.select_date)

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                textAlign = TextAlign.Center,
                text = "$startDateTitle - $endDateTitle",
                style = MaterialTheme.typography.titleLarge,
            )
        }

    )

    LaunchedEffect(state.selectedStartDateMillis, state.selectedEndDateMillis) {
        val currentZone = TimeZone.currentSystemDefault()

        val startDate = state.selectedStartDateMillis?.let { millis ->
            val localDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
            val startInstant = localDate.atStartOfDayIn(currentZone)
            TransactionDate(startInstant, currentZone)
        }

        val endDate = state.selectedEndDateMillis?.let { millis ->
            val localDate = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
            val endInstant = LocalDateTime(
                year = localDate.year,
                monthNumber = localDate.monthNumber,
                dayOfMonth = localDate.dayOfMonth,
                hour = 23,
                minute = 59,
                second = 59,
                nanosecond = 999_999_999
            ).toInstant(currentZone)
            TransactionDate(endInstant, currentZone)
        }

        onDatesSelected(startDate to endDate)
    }
}

@Composable
@Preview
private fun DashboardCalendarPreview() {
    AppTheme {
        DashboardDateRangeCalendar(
            modifier = Modifier,
            selectedDates = PreviewMocks.transactionDate to PreviewMocks.transactionDate,
            onDatesSelected = {}
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    AppTheme {
        DashboardCalendarButton(
            dateRange = PreviewMocks.transactionDate to PreviewMocks.transactionDate
        )
    }
}