package com.expensetracker.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.expensetracker.app.core.design.category.CategoryRegistry
import com.expensetracker.app.data.local.dao.BudgetDao
import com.expensetracker.app.data.local.dao.CategoryDao
import com.expensetracker.app.data.local.dao.TransactionDao
import com.expensetracker.app.data.local.entity.BudgetEntity
import com.expensetracker.app.data.local.entity.CategoryEntity
import com.expensetracker.app.data.local.entity.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        BudgetEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "expense_tracker_db_v2"
                )
                .fallbackToDestructiveMigration()
                .fallbackToDestructiveMigrationOnDowngrade()
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Seed built-in categories
            val now = System.currentTimeMillis()
            CategoryRegistry.AllDefaultCategories.forEach { cat ->
                val sql = """
                    INSERT OR IGNORE INTO categories 
                    (id, name, iconKey, colorHex, type, isBuiltIn, isArchived, sortOrder, createdAt, updatedAt) 
                    VALUES ('${cat.categoryId}', '${cat.name.replace("'", "''")}', '${cat.iconKey}', '${cat.colorHex}', '${cat.type.name}', 1, 0, ${cat.sortOrder}, $now, $now)
                """.trimIndent()
                try {
                    db.execSQL(sql)
                } catch (e: Exception) {
                    android.util.Log.e("AppDatabase", "Failed to seed category ${cat.categoryId}", e)
                }
            }
        }
    }
}

