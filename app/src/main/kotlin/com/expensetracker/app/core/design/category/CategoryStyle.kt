package com.expensetracker.app.core.design.category

import androidx.compose.ui.graphics.Color

enum class CategoryType {
    EXPENSE,
    INCOME,
    BOTH
}

data class CategoryStyle(
    val categoryId: String,
    val name: String,
    val iconKey: String,
    val colorHex: String,
    val type: CategoryType = CategoryType.EXPENSE,
    val isBuiltIn: Boolean = true,
    val isArchived: Boolean = false,
    val sortOrder: Int = 0
) {
    val color: Color
        get() = try {
            Color(android.graphics.Color.parseColor(colorHex))
        } catch (e: Exception) {
            Color(0xFF778CA3)
        }
}

object CategoryRegistry {
    val DefaultExpenseCategories = listOf(
        CategoryStyle("cat_food", "Food & Dining", "restaurant", "#FFA834", CategoryType.EXPENSE, sortOrder = 1),
        CategoryStyle("cat_groceries", "Groceries", "shopping_basket", "#2ED573", CategoryType.EXPENSE, sortOrder = 2),
        CategoryStyle("cat_transport", "Transport", "directions_car", "#3867D6", CategoryType.EXPENSE, sortOrder = 3),
        CategoryStyle("cat_shopping", "Shopping", "shopping_bag", "#8854D0", CategoryType.EXPENSE, sortOrder = 4),
        CategoryStyle("cat_home", "Home & Rent", "home", "#A55EEA", CategoryType.EXPENSE, sortOrder = 5),
        CategoryStyle("cat_bills", "Bills & Utilities", "receipt_long", "#0FB9B1", CategoryType.EXPENSE, sortOrder = 6),
        CategoryStyle("cat_health", "Health", "medical_services", "#FC5C65", CategoryType.EXPENSE, sortOrder = 7),
        CategoryStyle("cat_entertainment", "Entertainment", "movie", "#FD9644", CategoryType.EXPENSE, sortOrder = 8),
        CategoryStyle("cat_travel", "Travel", "flight", "#26DE81", CategoryType.EXPENSE, sortOrder = 9),
        CategoryStyle("cat_education", "Education", "school", "#45AAF2", CategoryType.EXPENSE, sortOrder = 10),
        CategoryStyle("cat_subscriptions", "Subscriptions", "subscriptions", "#778BEB", CategoryType.EXPENSE, sortOrder = 11),
        CategoryStyle("cat_personal", "Personal Care", "spa", "#70A1FF", CategoryType.EXPENSE, sortOrder = 12),
        CategoryStyle("cat_gifts", "Gifts & Donations", "redeem", "#FF6B81", CategoryType.EXPENSE, sortOrder = 13),
        CategoryStyle("cat_other_expense", "Other", "category", "#778CA3", CategoryType.EXPENSE, sortOrder = 14)
    )

    val DefaultIncomeCategories = listOf(
        CategoryStyle("cat_salary", "Salary", "payments", "#2ECC71", CategoryType.INCOME, sortOrder = 1),
        CategoryStyle("cat_freelance", "Freelance", "work", "#00D2D3", CategoryType.INCOME, sortOrder = 2),
        CategoryStyle("cat_business", "Business", "storefront", "#F39C12", CategoryType.INCOME, sortOrder = 3),
        CategoryStyle("cat_interest", "Interest", "account_balance", "#546DE5", CategoryType.INCOME, sortOrder = 4),
        CategoryStyle("cat_gift_income", "Gift", "card_giftcard", "#E84393", CategoryType.INCOME, sortOrder = 5),
        CategoryStyle("cat_other_income", "Other Income", "add_circle", "#10AC84", CategoryType.INCOME, sortOrder = 6)
    )

    val AllDefaultCategories = DefaultExpenseCategories + DefaultIncomeCategories

    private val categoryMap = AllDefaultCategories.associateBy { it.categoryId }

    fun getCategory(id: String): CategoryStyle {
        return categoryMap[id] ?: CategoryStyle(
            categoryId = id,
            name = "Unknown",
            iconKey = "category",
            colorHex = "#778CA3",
            type = CategoryType.EXPENSE
        )
    }

    val CuratedPaletteColors = listOf(
        "#FFA834", // Warm amber
        "#2ED573", // Fresh green
        "#3867D6", // Clear blue
        "#8854D0", // Violet
        "#A55EEA", // Soft lavender
        "#0FB9B1", // Cool cyan
        "#FC5C65", // Rose
        "#FD9644", // Coral
        "#26DE81", // Teal
        "#45AAF2", // Periwinkle
        "#778BEB", // Purple
        "#70A1FF", // Sage
        "#FF6B81", // Pink
        "#778CA3", // Neutral slate
        "#2ECC71", // Emerald
        "#00D2D3", // Bright cyan
        "#F39C12", // Orange amber
        "#546DE5"  // Deep indigo
    )

    fun parseHexColor(hex: String, fallback: Color = Color(0xFF778CA3)): Color {
        return try {
            Color(android.graphics.Color.parseColor(hex))
        } catch (e: Exception) {
            fallback
        }
    }
}

val CuratedPalette = CategoryRegistry.CuratedPaletteColors

