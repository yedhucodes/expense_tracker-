package com.expensetracker.app.domain.model

import com.expensetracker.app.core.design.category.CategoryType

data class Category(
    val id: String,
    val name: String,
    val iconKey: String,
    val colorHex: String,
    val type: CategoryType = CategoryType.EXPENSE,
    val isBuiltIn: Boolean = false,
    val isArchived: Boolean = false,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

