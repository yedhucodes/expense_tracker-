package com.expensetracker.app.core.time

import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DatePeriodUtil {

    fun getMonthRange(year: Int, month: Int, zoneId: ZoneId = ZoneId.systemDefault()): Pair<Long, Long> {
        val ym = YearMonth.of(year, month)
        val startInstant = ym.atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endInstant = ym.atEndOfMonth().atTime(23, 59, 59, 999_000_000).atZone(zoneId).toInstant().toEpochMilli()
        return Pair(startInstant, endInstant)
    }

    fun formatMonthYear(year: Int, month: Int, locale: Locale = Locale.getDefault()): String {
        val ym = YearMonth.of(year, month)
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)
        return ym.format(formatter)
    }

    fun formatDate(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault(), locale: Locale = Locale.getDefault()): String {
        val date = Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalDate()
        val today = LocalDate.now(zoneId)

        return when (date) {
            today -> "Today"
            today.minusDays(1) -> "Yesterday"
            else -> {
                val formatter = DateTimeFormatter.ofPattern("EEE, MMM d", locale)
                date.format(formatter)
            }
        }
    }

    fun formatFullDate(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault(), locale: Locale = Locale.getDefault()): String {
        val date = Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalDate()
        val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", locale)
        return date.format(formatter)
    }

    fun formatMediumDate(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault(), locale: Locale = Locale.getDefault()): String {
        return formatFullDate(epochMillis, zoneId, locale)
    }

    fun formatTime(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault(), locale: Locale = Locale.getDefault()): String {
        val time = Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalTime()
        val formatter = DateTimeFormatter.ofPattern("h:mm a", locale)
        return time.format(formatter)
    }

    fun getPreviousMonth(year: Int, month: Int): Pair<Int, Int> {
        val ym = YearMonth.of(year, month).minusMonths(1)
        return Pair(ym.year, ym.monthValue)
    }

    fun getNextMonth(year: Int, month: Int): Pair<Int, Int> {
        val ym = YearMonth.of(year, month).plusMonths(1)
        return Pair(ym.year, ym.monthValue)
    }

    fun getCurrentYearMonth(): Pair<Int, Int> {
        val now = LocalDate.now()
        return Pair(now.year, now.monthValue)
    }
}

