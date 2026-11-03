package com.expensetracker.app.core.design.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalAtm
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Redeem
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.ShoppingBasket
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.vector.ImageVector

object IconRegistry {
    private val iconMap: Map<String, ImageVector> = mapOf(
        "restaurant" to Icons.Rounded.Restaurant,
        "shopping_basket" to Icons.Rounded.ShoppingBasket,
        "directions_car" to Icons.Rounded.DirectionsCar,
        "shopping_bag" to Icons.Rounded.ShoppingBag,
        "home" to Icons.Rounded.Home,
        "receipt_long" to Icons.AutoMirrored.Rounded.ReceiptLong,
        "medical_services" to Icons.Rounded.MedicalServices,
        "movie" to Icons.Rounded.Movie,
        "flight" to Icons.Rounded.Flight,
        "school" to Icons.Rounded.School,
        "subscriptions" to Icons.Rounded.Subscriptions,
        "spa" to Icons.Rounded.Spa,
        "redeem" to Icons.Rounded.Redeem,
        "category" to Icons.Rounded.Category,
        "payments" to Icons.Rounded.Payments,
        "work" to Icons.Rounded.Work,
        "storefront" to Icons.Rounded.Storefront,
        "account_balance" to Icons.Rounded.AccountBalance,
        "card_giftcard" to Icons.Rounded.CardGiftcard,
        "add_circle" to Icons.Rounded.AddCircle,
        "wallet" to Icons.Rounded.AccountBalanceWallet,
        "cash" to Icons.Rounded.LocalAtm,
        "card" to Icons.Rounded.CreditCard,
        "bank" to Icons.Rounded.AccountBalance,
        "bar_chart" to Icons.Rounded.BarChart,
        "settings" to Icons.Rounded.Settings,
        "search" to Icons.Rounded.Search,
        "filter" to Icons.Rounded.FilterList,
        "add" to Icons.Rounded.Add,
        "edit" to Icons.Rounded.Edit,
        "delete" to Icons.Rounded.Delete,
        "check" to Icons.Rounded.Check,
        "close" to Icons.Rounded.Close,
        "clear" to Icons.Rounded.Clear,
        "more" to Icons.Rounded.MoreVert,
        "arrow_back" to Icons.AutoMirrored.Rounded.ArrowBack,
        "arrow_forward" to Icons.AutoMirrored.Rounded.ArrowForward
    )

    fun getIcon(key: String): ImageVector {
        return iconMap[key] ?: Icons.Rounded.Category
    }

    fun getAllAvailableKeys(): List<String> {
        return iconMap.keys.toList()
    }

    val AvailableIcons: List<String>
        get() = getAllAvailableKeys()
}
