package com.pocketpal.data.repository

import com.pocketpal.data.local.CategoryDao
import com.pocketpal.data.local.CategoryEntity
import com.pocketpal.data.local.PocketPalDatabase
import com.pocketpal.data.local.TransactionDao
import com.pocketpal.data.local.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class TransactionRepository(private val database: PocketPalDatabase) {
    
    private val dao: TransactionDao = database.transactionDao()
    
    fun getAllTransactions(): Flow<List<TransactionEntity>> = dao.getAllTransactions()
    
    fun getTransactionById(id: String): Flow<TransactionEntity?> = dao.getTransactionByIdFlow(id)
    
    fun getTransactionsByAccount(accountId: String): Flow<List<TransactionEntity>> = 
        dao.getTransactionsByAccount(accountId)
    
    fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>> = 
        dao.getTransactionsByCategory(category)
    
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>> = 
        dao.getTransactionsByType(type)
    
    fun getTransactionsBetweenDates(startDate: String, endDate: String): Flow<List<TransactionEntity>> = 
        dao.getTransactionsBetweenDates(startDate, endDate)
    
    fun getExpensesBetweenDates(startDate: String, endDate: String): Flow<List<TransactionEntity>> = 
        dao.getExpensesBetweenDates(startDate, endDate)
    
    fun getTotalExpenses(startDate: String, endDate: String): Flow<Double?> = 
        dao.getTotalExpenses(startDate, endDate)
    
    fun getTotalIncome(startDate: String, endDate: String): Flow<Double?> = 
        dao.getTotalIncome(startDate, endDate)
    
    fun getTransactionCount(): Flow<Int> = dao.getTransactionCount()
    
    suspend fun addTransaction(
        type: String,
        amount: Double,
        category: String,
        accountId: String,
        toAccountId: String? = null,
        date: String,
        note: String = "",
        recurringId: String? = null
    ) {
        val transaction = TransactionEntity(
            id = UUID.randomUUID().toString(),
            type = type,
            amount = amount,
            category = category,
            accountId = accountId,
            toAccountId = toAccountId,
            date = date,
            note = note,
            recurringId = recurringId
        )
        dao.insertTransaction(transaction)
    }
    
    suspend fun updateTransaction(transaction: TransactionEntity) {
        dao.updateTransaction(
            transaction.copy(updatedAt = System.currentTimeMillis())
        )
    }
    
    suspend fun deleteTransaction(transaction: TransactionEntity) {
        dao.deleteTransaction(transaction)
    }
    
    suspend fun deleteTransactionById(id: String) {
        dao.deleteTransactionById(id)
    }
    
    suspend fun deleteAllTransactions() {
        dao.deleteAllTransactions()
    }
}