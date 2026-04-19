package com.pocketpal.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val type: String, // "INCOME", "EXPENSE", "TRANSFER"
    val amount: Double,
    val category: String,
    val accountId: String,
    val toAccountId: String? = null,
    val date: String, // ISO 8601 format
    val note: String = "",
    val recurringId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)