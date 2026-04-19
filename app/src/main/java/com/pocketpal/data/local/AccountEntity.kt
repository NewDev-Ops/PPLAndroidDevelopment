package com.pocketpal.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String, // "BANK", "CREDIT", "CASH", "SAVINGS", "INVESTMENTS"
    val balance: Double,
    val color: String = "#3B82F6",
    val currency: String = "USD",
    val icon: String = "wallet",
    val investmentConfig: String? = null, // JSON string for investment config
    val lastReturnsApplied: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)