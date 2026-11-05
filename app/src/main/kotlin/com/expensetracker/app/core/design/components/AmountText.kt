package com.expensetracker.app.core.design.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.expensetracker.app.core.design.theme.AppTheme
import com.expensetracker.app.core.money.MoneyFormatter

enum class AmountDisplayStyle {
    DisplayHero,
    Headline,
    Title,
    TitleCard,
    Body,
    BodyStandard,
    Caption,
    ListAmount
}

@Composable
fun AmountText(
    amountMinor: Long,
    currencyCode: String,
    modifier: Modifier = Modifier,
    isIncome: Boolean? = null,
    includePrefix: Boolean = false,
    displayStyle: AmountDisplayStyle = AmountDisplayStyle.Title,
    overrideColor: Color? = null
) {
    val formattedString = MoneyFormatter.format(
        amountMinor = amountMinor,
        currencyCode = currencyCode,
        includePrefix = includePrefix && isIncome != null,
        isIncome = isIncome == true
    )

    val textColor = overrideColor ?: when (isIncome) {
        true -> AppTheme.colors.income
        false -> AppTheme.colors.expense
        null -> AppTheme.colors.textPrimary
    }

    val textStyle: TextStyle = (when (displayStyle) {
        AmountDisplayStyle.DisplayHero -> AppTheme.typography.displayLarge
        AmountDisplayStyle.Headline -> AppTheme.typography.headlineLarge
        AmountDisplayStyle.Title, AmountDisplayStyle.TitleCard -> AppTheme.typography.titleLarge
        AmountDisplayStyle.Body, AmountDisplayStyle.BodyStandard, AmountDisplayStyle.ListAmount -> AppTheme.typography.bodyLarge
        AmountDisplayStyle.Caption -> AppTheme.typography.bodySmall
    }).copy(fontFeatureSettings = "tnum")

    Text(
        text = formattedString,
        style = textStyle,
        color = textColor,
        modifier = modifier
    )
}

