package com.expensetracker.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.expensetracker.app.core.design.category.CategoryType
import com.expensetracker.app.domain.model.Category

@Entity(
    tableName = "categories",
    indices = [
        Index("type"),
        Index("isArchived")
    ]
)
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val iconKey: String,
    val colorHex: String,
    val type: String, // "EXPENSE", "INCOME", "BOTH"
    val isBuiltIn: Boolean,
    val isArchived: Boolean = false,
    val sortOrder: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): Category = Category(
        id = id,
        name = name,
        iconKey = iconKey,
        colorHex = colorHex,
        type = try { CategoryType.valueOf(type) } catch (e: Exception) { CategoryType.EXPENSE },
        isBuiltIn = isBuiltIn,
        isArchived = isArchived,
        sortOrder = sortOrder,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(domain: Category): CategoryEntity = CategoryEntity(
            id = domain.id,
            name = domain.name,
            iconKey = domain.iconKey,
            colorHex = domain.colorHex,
            type = domain.type.name,
            isBuiltIn = domain.isBuiltIn,
            isArchived = domain.isArchived,
            sortOrder = domain.sortOrder,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}

