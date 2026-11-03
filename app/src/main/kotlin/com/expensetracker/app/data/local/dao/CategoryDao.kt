package com.expensetracker.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.expensetracker.app.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, name ASC")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE isArchived = 0 ORDER BY sortOrder ASC, name ASC")
    fun observeActive(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    fun getCategoryById(id: String): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(categories: List<CategoryEntity>): List<Long>

    @Query("UPDATE categories SET isArchived = 1, updatedAt = :updatedAt WHERE id = :id")
    fun archiveCategory(id: String, updatedAt: Long): Int

    @Query("DELETE FROM categories WHERE id = :id")
    fun deleteCategory(id: String): Int

    @Query("SELECT COUNT(*) FROM categories")
    fun countCategories(): Int
}
