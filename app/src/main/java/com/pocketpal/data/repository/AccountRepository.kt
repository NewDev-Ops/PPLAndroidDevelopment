package com.pocketpal.data.repository

import com.pocketpal.data.local.AccountDao
import com.pocketpal.data.local.AccountEntity
import com.pocketpal.data.local.PocketPalDatabase
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class AccountRepository(private val database: PocketPalDatabase) {
    
    private val dao: AccountDao = database.accountDao()
    
    fun getAllAccounts(): Flow<List<AccountEntity>> = dao.getAllAccounts()
    
    fun getAccountById(id: String): Flow<AccountEntity?> = dao.getAccountByIdFlow(id)
    
    fun getAccountsByType(type: String): Flow<List<AccountEntity>> = 
        dao.getAccountsByType(type)
    
    fun getTotalAssets(): Flow<Double?> = dao.getTotalAssets()
    
    fun getTotalLiabilities(): Flow<Double?> = dao.getTotalLiabilities()
    
    suspend fun addAccount(
        name: String,
        type: String,
        balance: Double,
        color: String = "#3B82F6",
        currency: String = "USD",
        icon: String = "wallet",
        investmentConfig: String? = null
    ) {
        val account = AccountEntity(
            id = UUID.randomUUID().toString(),
            name = name,
            type = type,
            balance = balance,
            color = color,
            currency = currency,
            icon = icon,
            investmentConfig = investmentConfig
        )
        dao.insertAccount(account)
    }
    
    suspend fun updateAccount(account: AccountEntity) {
        dao.updateAccount(
            account.copy(updatedAt = System.currentTimeMillis())
        )
    }
    
    suspend fun updateAccountBalance(id: String, newBalance: Double) {
        val account = dao.getAccountById(id)
        account?.let {
            dao.updateAccount(it.copy(
                balance = newBalance,
                updatedAt = System.currentTimeMillis()
            ))
        }
    }
    
    suspend fun deleteAccount(account: AccountEntity) {
        dao.deleteAccount(account)
    }
    
    suspend fun deleteAccountById(id: String) {
        dao.deleteAccountById(id)
    }
}