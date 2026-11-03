package com.expensetracker.app

import com.expensetracker.app.core.money.MoneyFormatter
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class MoneyFormatterTest {

    @Test
    fun testParseToMinorStandardCurrencies() {
        // USD (2 decimal places)
        assertEquals(1050L, MoneyFormatter.parseToMinor("10.50", "USD"))
        assertEquals(1000L, MoneyFormatter.parseToMinor("10", "USD"))
        assertEquals(1050L, MoneyFormatter.parseToMinor("10,50", "USD"))
        assertEquals(5L, MoneyFormatter.parseToMinor("0.05", "USD"))
        assertEquals(0L, MoneyFormatter.parseToMinor("0", "USD"))
        assertEquals(0L, MoneyFormatter.parseToMinor("", "USD"))
    }

    @Test
    fun testParseToMinorZeroDecimalCurrencies() {
        // JPY (0 decimal places)
        assertEquals(1500L, MoneyFormatter.parseToMinor("1500", "JPY"))
        assertEquals(1500L, MoneyFormatter.parseToMinor("1500.99", "JPY")) // drops fraction
    }

    @Test
    fun testParseToMinorThreeDecimalCurrencies() {
        // BHD / KWD (3 decimal places)
        assertEquals(1250L, MoneyFormatter.parseToMinor("1.250", "BHD"))
        assertEquals(1005L, MoneyFormatter.parseToMinor("1.005", "BHD"))
    }

    @Test
    fun testFormattingStandardUsd() {
        val formatted = MoneyFormatter.format(
            amountMinor = 142550L,
            currencyCode = "USD",
            locale = Locale.US,
            includePrefix = true,
            isIncome = false
        )
        assertEquals("-$1,425.50", formatted)

        val incomeFormatted = MoneyFormatter.format(
            amountMinor = 350000L,
            currencyCode = "USD",
            locale = Locale.US,
            includePrefix = true,
            isIncome = true
        )
        assertEquals("+$3,500.00", incomeFormatted)
    }

    @Test
    fun testFormattingZeroDecimalJpy() {
        val formatted = MoneyFormatter.format(
            amountMinor = 5000L,
            currencyCode = "JPY",
            locale = Locale.JAPAN,
            includePrefix = false
        )
        // Check contains 5,000
        assert(formatted.contains("5,000"))
    }
}

