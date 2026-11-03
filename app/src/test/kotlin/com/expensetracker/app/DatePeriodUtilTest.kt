package com.expensetracker.app

import com.expensetracker.app.core.time.DatePeriodUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class DatePeriodUtilTest {

    @Test
    fun testMonthRangeBoundaries() {
        val zone = ZoneId.of("UTC")
        val (start, end) = DatePeriodUtil.getMonthRange(2026, 11, zone)

        val startDate = Instant.ofEpochMilli(start).atZone(zone).toLocalDate()
        val endDate = Instant.ofEpochMilli(end).atZone(zone).toLocalDate()

        assertEquals(LocalDate.of(2026, 11, 1), startDate)
        assertEquals(LocalDate.of(2026, 11, 30), endDate)
        assertTrue(end > start)
    }

    @Test
    fun testLeapYearFebruary() {
        val zone = ZoneId.of("UTC")
        // 2024 is leap year
        val (_, endLeap) = DatePeriodUtil.getMonthRange(2024, 2, zone)
        val endLeapDate = Instant.ofEpochMilli(endLeap).atZone(zone).toLocalDate()
        assertEquals(29, endLeapDate.dayOfMonth)

        // 2026 is non-leap
        val (_, endNonLeap) = DatePeriodUtil.getMonthRange(2026, 2, zone)
        val endNonLeapDate = Instant.ofEpochMilli(endNonLeap).atZone(zone).toLocalDate()
        assertEquals(28, endNonLeapDate.dayOfMonth)
    }

    @Test
    fun testMonthNavigationAcrossYearBoundary() {
        val (prevYear, prevMonth) = DatePeriodUtil.getPreviousMonth(2026, 1)
        assertEquals(2025, prevYear)
        assertEquals(12, prevMonth)

        val (nextYear, nextMonth) = DatePeriodUtil.getNextMonth(2026, 12)
        assertEquals(2027, nextYear)
        assertEquals(1, nextMonth)
    }
}

