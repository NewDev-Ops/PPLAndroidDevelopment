package com.pocketpal.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    
    @Query("SELECT * FROM accounts ORDER BY name ASC")
    fun getAllAccounts(): Flow<List<AccountEntity>>
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getAccountById(id: String): AccountEntity?
    
    @Query("SELECT * FROM accounts WHERE id = :id")
    fun getAccountByIdFlow(id: String): Flow<AccountEntity?>
    
    @Query("SELECT * FROM accounts WHERE type = :type ORDER BY name ASC")
    fun getAccountsByType(type: String): Flow<List<AccountEntity>>
    
    @Query("SELECT SUM(balance) FROM accounts WHERE type != 'CREDIT'")
    fun getTotalAssets(): Flow<Double?>
    
    @Query("SELECT SUM(balance) FROM accounts WHERE type = 'CREDIT'")
    fun getTotalLiabilities(): Flow<Double?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<AccountEntity>)
    
    @Update
    suspend fun updateAccount(account: AccountEntity)
    
    @Delete
    suspend fun deleteAccount(account: AccountEntity)
    
    @Query("DELETE FROM accounts WHERE id = :id")
    suspend fun deleteAccountById(id: String)
}