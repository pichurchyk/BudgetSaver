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
import com.pichurchyk.budgetsaver.ui.theme.AppTheme
import kotlinx.datetime.Instant

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
        initialSelectedStartDateMillis = selectedDates.first?.dateInstant?.toEpochMilliseconds(),
        initialSelectedEndDateMillis = selectedDates.second?.dateInstant?.toEpochMilliseconds(),
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
                    .padding(horizontal = 4.dp, vertical = 8.dp),                textAlign = TextAlign.Center,
                text = stringResource(R.string.select_date_range),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        headline = {
            val startDate = state.selectedStartDateMillis?.let { Instant.fromEpochMilliseconds(it) }
            val endDate = state.selectedEndDateMillis?.let { Instant.fromEpochMilliseconds(it) }

            val startDateTitle =
                startDate?.let { DateUtils.toStringWithPattern(it, "MMM dd, yyyy") }
                    ?: stringResource(R.string.select_date)
            val endDateTitle = endDate?.let { DateUtils.toStringWithPattern(it, "MMM dd, yyyy") }
                ?: stringResource(R.string.select_date)

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
        val startDate = state.selectedStartDateMillis?.let { Instant.fromEpochMilliseconds(it) }
        val endDate = state.selectedEndDateMillis?.let { Instant.fromEpochMilliseconds(it) }?.toTheEndOfDay()

        onDatesSelected(startDate?.let { TransactionDate.createWithDefaultTimeZone(it) } to endDate?.let {
            TransactionDate.createWithDefaultTimeZone(
                it
            )
        })
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