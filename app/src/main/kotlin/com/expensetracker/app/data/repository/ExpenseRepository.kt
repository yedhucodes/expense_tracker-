package com.expensetracker.app.data.repository

import com.expensetracker.app.core.design.category.CategoryRegistry
import com.expensetracker.app.core.money.MoneyFormatter
import com.expensetracker.app.core.time.DatePeriodUtil
import com.expensetracker.app.data.local.AppDatabase
import com.expensetracker.app.data.local.entity.BudgetEntity
import com.expensetracker.app.data.local.entity.CategoryEntity
import com.expensetracker.app.data.local.entity.TransactionEntity
import com.expensetracker.app.domain.model.Budget
import com.expensetracker.app.domain.model.BudgetThresholdState
import com.expensetracker.app.domain.model.BudgetWithProgress
import com.expensetracker.app.domain.model.Category
import com.expensetracker.app.domain.model.CategorySpending
import com.expensetracker.app.domain.model.MonthlySummary
import com.expensetracker.app.domain.model.Transaction
import com.expensetracker.app.domain.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ExpenseRepository(
    private val database: AppDatabase
) {
    private val transactionDao = database.transactionDao()
    private val categoryDao = database.categoryDao()
    private val budgetDao = database.budgetDao()

    // Transactions
    fun observeAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.observeAll().map { list -> list.map { it.toDomain() } }
    }

    fun observeTransactionsForMonth(year: Int, month: Int): Flow<List<Transaction>> {
        val (start, end) = DatePeriodUtil.getMonthRange(year, month)
        return transactionDao.observeByDateRange(start, end).map { list -> list.map { it.toDomain() } }
    }

    fun observeRecentTransactions(limit: Int = 5): Flow<List<Transaction>> {
        return transactionDao.observeRecent(limit).map { list -> list.map { it.toDomain() } }
    }

    fun getTransactionById(id: String): Flow<Transaction?> = flow {
        val entity = withContext(Dispatchers.IO) { transactionDao.getTransactionById(id) }
        emit(entity?.toDomain())
    }

    suspend fun saveTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        val entity = TransactionEntity.fromDomain(transaction.copy(updatedAt = System.currentTimeMillis()))
        transactionDao.upsert(entity)
    }

    suspend fun deleteTransaction(id: String, soft: Boolean = true) = withContext(Dispatchers.IO) {
        if (soft) {
            transactionDao.softDelete(id, System.currentTimeMillis())
        } else {
            transactionDao.hardDelete(id)
        }
    }

    suspend fun restoreTransaction(id: String) = withContext(Dispatchers.IO) {
        transactionDao.restore(id, System.currentTimeMillis())
    }

    // Monthly Summary Calculation
    fun observeMonthlySummary(year: Int, month: Int, currencyCode: String = "USD"): Flow<MonthlySummary> {
        return observeTransactionsForMonth(year, month).map { transactions ->
            var spent = 0L
            var income = 0L

            transactions.forEach { tx ->
                if (tx.currencyCode.equals(currencyCode, ignoreCase = true)) {
                    when (tx.type) {
                        TransactionType.EXPENSE -> spent += tx.amountMinor
                        TransactionType.INCOME -> income += tx.amountMinor
                    }
                }
            }

            MonthlySummary(
                year = year,
                month = month,
                totalSpentMinor = spent,
                totalIncomeMinor = income,
                netCashFlowMinor = income - spent,
                currencyCode = currencyCode,
                transactionCount = transactions.size
            )
        }
    }

    // Category Breakdown Calculation
    fun observeCategoryBreakdown(year: Int, month: Int): Flow<List<CategorySpending>> {
        return combine(
            observeTransactionsForMonth(year, month),
            observeCategories()
        ) { transactions, categories ->
            val categoryMap = categories.associateBy { it.id }
            val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
            val totalExpenseMinor = expenses.sumOf { it.amountMinor }

            val grouped = expenses.groupBy { it.categoryId }

            grouped.map { (catId, txList) ->
                val spent = txList.sumOf { it.amountMinor }
                val cat = categoryMap[catId] ?: CategoryRegistry.getCategory(catId).let {
                    Category(it.categoryId, it.name, it.iconKey, it.colorHex)
                }

                val percentage = if (totalExpenseMinor > 0) {
                    (spent.toFloat() / totalExpenseMinor.toFloat()) * 100f
                } else 0f

                CategorySpending(
                    categoryId = catId,
                    categoryName = cat.name,
                    iconKey = cat.iconKey,
                    colorHex = cat.colorHex,
                    spentMinor = spent,
                    percentageOfTotal = percentage,
                    transactionCount = txList.size
                )
            }.sortedByDescending { it.spentMinor }
        }
    }

    // Categories
    fun observeCategories(): Flow<List<Category>> {
        return categoryDao.observeActive().map { list ->
            if (list.isEmpty()) {
                CategoryRegistry.AllDefaultCategories.map {
                    Category(
                        id = it.categoryId,
                        name = it.name,
                        iconKey = it.iconKey,
                        colorHex = it.colorHex,
                        type = it.type,
                        isBuiltIn = it.isBuiltIn,
                        sortOrder = it.sortOrder
                    )
                }
            } else {
                list.map { it.toDomain() }
            }
        }
    }

    suspend fun saveCategory(category: Category) = withContext(Dispatchers.IO) {
        categoryDao.upsert(CategoryEntity.fromDomain(category.copy(updatedAt = System.currentTimeMillis())))
    }

    suspend fun archiveCategory(id: String) = withContext(Dispatchers.IO) {
        categoryDao.archiveCategory(id, System.currentTimeMillis())
    }

    suspend fun reassignCategory(oldCategoryId: String, newCategoryId: String) = withContext(Dispatchers.IO) {
        transactionDao.reassignCategory(oldCategoryId, newCategoryId, System.currentTimeMillis())
    }

    suspend fun countTransactionsForCategory(categoryId: String): Int = withContext(Dispatchers.IO) {
        transactionDao.countTransactionsForCategory(categoryId)
    }

    // Budgets & Progress Calculation
    fun observeBudgetsWithProgress(year: Int, month: Int): Flow<List<BudgetWithProgress>> {
        return combine(
            budgetDao.observeBudgetsForMonth(year, month),
            observeTransactionsForMonth(year, month)
        ) { budgetEntities, transactions ->
            val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
            val overallSpent = expenses.sumOf { it.amountMinor }
            val categorySpentMap = expenses.groupBy { it.categoryId }.mapValues { (_, list) -> list.sumOf { it.amountMinor } }

            budgetEntities.map { entity ->
                val budget = entity.toDomain()
                val spent = if (budget.categoryId == null) {
                    overallSpent
                } else {
                    categorySpentMap[budget.categoryId] ?: 0L
                }

                val remaining = budget.amountMinor - spent
                val ratio = if (budget.amountMinor > 0) {
                    spent.toFloat() / budget.amountMinor.toFloat()
                } else 0f

                val thresholdState = when {
                    ratio >= 1.0f -> BudgetThresholdState.OVER_100
                    ratio >= 0.90f -> BudgetThresholdState.DANGER_90
                    ratio >= 0.75f -> BudgetThresholdState.WARNING_75
                    else -> BudgetThresholdState.NORMAL
                }

                BudgetWithProgress(
                    budget = budget,
                    spentMinor = spent,
                    remainingMinor = remaining,
                    progressPercent = ratio.coerceAtLeast(0f),
                    isOverBudget = spent > budget.amountMinor,
                    thresholdState = thresholdState
                )
            }.sortedBy { it.budget.categoryId != null } // Overall budget first
        }
    }

    suspend fun saveBudget(budget: Budget) = withContext(Dispatchers.IO) {
        budgetDao.upsert(BudgetEntity.fromDomain(budget.copy(updatedAt = System.currentTimeMillis())))
    }

    suspend fun deleteBudget(id: String) = withContext(Dispatchers.IO) {
        budgetDao.deleteBudget(id)
    }

    // CSV Export
    suspend fun exportTransactionsCsv(year: Int? = null, month: Int? = null): String {
        val (start, end) = if (year != null && month != null) {
            DatePeriodUtil.getMonthRange(year, month)
        } else {
            Pair(0L, Long.MAX_VALUE)
        }

        val entities = if (year != null && month != null) {
            transactionDao.observeByDateRange(start, end).first()
        } else {
            transactionDao.observeAll().first()
        }

        val categories = CategoryRegistry.AllDefaultCategories.associateBy { it.categoryId }

        val builder = StringBuilder()
        builder.append("ID,Date,Time,Type,Category,Merchant,Note,PaymentMethod,Amount,Currency\n")

        entities.forEach { tx ->
            val dateStr = DatePeriodUtil.formatFullDate(tx.occurredAt)
            val timeStr = DatePeriodUtil.formatTime(tx.occurredAt)
            val catName = categories[tx.categoryId]?.name ?: tx.categoryId
            val amountFormatted = MoneyFormatter.format(tx.amountMinor, tx.currencyCode)

            fun escape(field: String?): String {
                val f = field ?: ""
                return if (f.contains(",") || f.contains("\"") || f.contains("\n")) {
                    "\"${f.replace("\"", "\"\"")}\""
                } else f
            }

            builder.append("${tx.id},")
            builder.append("${escape(dateStr)},")
            builder.append("${escape(timeStr)},")
            builder.append("${tx.type},")
            builder.append("${escape(catName)},")
            builder.append("${escape(tx.merchant)},")
            builder.append("${escape(tx.note)},")
            builder.append("${escape(tx.paymentMethod)},")
            builder.append("${escape(amountFormatted)},")
            builder.append("${tx.currencyCode}\n")
        }

        return builder.toString()
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        transactionDao.clearAll()
        budgetDao.clearAll()
    }
}
