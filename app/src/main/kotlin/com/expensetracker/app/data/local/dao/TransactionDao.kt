package com.expensetracker.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.expensetracker.app.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 ORDER BY occurredAt DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 AND occurredAt >= :startTime AND occurredAt <= :endTime ORDER BY occurredAt DESC")
    fun observeByDateRange(startTime: Long, endTime: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE isDeleted = 0 ORDER BY occurredAt DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    fun getTransactionById(id: String): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(transactions: List<TransactionEntity>): List<Long>

    @Query("UPDATE transactions SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :id")
    fun softDelete(id: String, updatedAt: Long): Int

    @Query("UPDATE transactions SET isDeleted = 0, updatedAt = :updatedAt WHERE id = :id")
    fun restore(id: String, updatedAt: Long): Int

    @Query("DELETE FROM transactions WHERE id = :id")
    fun hardDelete(id: String): Int

    @Query("UPDATE transactions SET categoryId = :newCategoryId, updatedAt = :updatedAt WHERE categoryId = :oldCategoryId")
    fun reassignCategory(oldCategoryId: String, newCategoryId: String, updatedAt: Long): Int

    @Query("SELECT COUNT(*) FROM transactions WHERE categoryId = :categoryId AND isDeleted = 0")
    fun countTransactionsForCategory(categoryId: String): Int

    @Query("DELETE FROM transactions")
    fun clearAll(): Int
}
