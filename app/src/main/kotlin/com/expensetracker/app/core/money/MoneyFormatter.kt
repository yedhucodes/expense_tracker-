package com.expensetracker.app.core.money

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.pow

object MoneyFormatter {

    fun format(
        amountMinor: Long,
        currencyCode: String = "USD",
        locale: Locale = Locale.getDefault(),
        includePrefix: Boolean = false,
        isIncome: Boolean = false
    ): String {
        val currency = try {
            Currency.getInstance(currencyCode)
        } catch (e: Exception) {
            Currency.getInstance("USD")
        }

        val fractionDigits = currency.defaultFractionDigits.coerceAtLeast(0)
        val divisor = 10.0.pow(fractionDigits.toDouble()).toLong().coerceAtLeast(1L)
        val major = amountMinor / divisor
        val minorRemainder = amountMinor % divisor

        val format = NumberFormat.getCurrencyInstance(locale).apply {
            this.currency = currency
            minimumFractionDigits = fractionDigits
            maximumFractionDigits = fractionDigits
        }

        // Calculate double strictly for the locale currency string formatting of whole+fraction
        val decimalVal = major.toDouble() + (minorRemainder.toDouble() / divisor.toDouble())
        val formatted = format.format(decimalVal)

        return if (includePrefix) {
            if (isIncome) "+$formatted" else "-$formatted"
        } else {
            formatted
        }
    }

    fun parseToMinor(
        rawInput: String,
        currencyCode: String = "USD"
    ): Long {
        val currency = try {
            Currency.getInstance(currencyCode)
        } catch (e: Exception) {
            Currency.getInstance("USD")
        }
        val fractionDigits = currency.defaultFractionDigits.coerceAtLeast(0)
        val cleaned = rawInput.trim().replace(",", ".").filter { it.isDigit() || it == '.' }
        if (cleaned.isEmpty()) return 0L

        val parts = cleaned.split(".")
        val wholePart = parts[0].toLongOrNull() ?: 0L
        if (fractionDigits == 0) return wholePart

        val multiplier = 10.0.pow(fractionDigits.toDouble()).toLong()
        val fracStr = if (parts.size > 1) parts[1].padEnd(fractionDigits, '0').take(fractionDigits) else "".padEnd(fractionDigits, '0')
        val fracPart = fracStr.toLongOrNull() ?: 0L

        return (wholePart * multiplier) + fracPart
    }

    fun getSymbol(currencyCode: String, locale: Locale = Locale.getDefault()): String {
        return try {
            Currency.getInstance(currencyCode).getSymbol(locale)
        } catch (e: Exception) {
            currencyCode
        }
    }
}

