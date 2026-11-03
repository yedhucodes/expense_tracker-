package com.expensetracker.app

import com.expensetracker.app.core.design.category.CategoryRegistry
import com.expensetracker.app.core.design.category.CategoryType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CategoryRegistryTest {

    @Test
    fun testAllBuiltInCategoriesPresent() {
        val all = CategoryRegistry.AllDefaultCategories
        assertTrue(all.isNotEmpty())
        assertTrue(all.any { it.categoryId == "cat_food" })
        assertTrue(all.any { it.categoryId == "cat_home" })
        assertTrue(all.any { it.categoryId == "cat_transport" })
        assertTrue(all.any { it.categoryId == "cat_salary" })
    }

    @Test
    fun testCategoryLookupFallback() {
        val food = CategoryRegistry.getCategory("cat_food")
        assertEquals("Food & Dining", food.name)

        val unknown = CategoryRegistry.getCategory("cat_non_existent")
        assertNotNull(unknown)
        assertEquals("Unknown", unknown.name)
    }

    @Test
    fun testExpenseVsIncomeTypes() {
        val food = CategoryRegistry.getCategory("cat_food")
        assertEquals(CategoryType.EXPENSE, food.type)

        val salary = CategoryRegistry.getCategory("cat_salary")
        assertEquals(CategoryType.INCOME, salary.type)
    }
}

