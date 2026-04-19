package com.pocketpal.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    
    @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: String): TransactionEntity?
    
    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getTransactionByIdFlow(id: String): Flow<TransactionEntity?>
    
    @Query("SELECT * FROM transactions WHERE accountId = :accountId ORDER BY date DESC")
    fun getTransactionsByAccount(accountId: String): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsBetweenDates(startDate: String, endDate: String): Flow<List<TransactionEntity>>
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND category = :category AND date BETWEEN :startDate AND :endDate")
    fun getTotalExpensesByCategory(category: String, startDate: String, endDate: String): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate")
    fun getTotalExpenses(startDate: String, endDate: String): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME' AND date BETWEEN :startDate AND :endDate")
    fun getTotalIncome(startDate: String, endDate: String): Flow<Double?>
    
    @Query("SELECT COUNT(*) FROM transactions")
    fun getTransactionCount(): Flow<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)
    
    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)
    
    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
    
    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: String)
    
    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
    
    @Query("DELETE FROM transactions WHERE accountId = :accountId")
    suspend fun deleteTransactionsByAccount(accountId: String)
}