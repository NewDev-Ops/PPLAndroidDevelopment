package com.pocketpal.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val type: String, // "INCOME", "EXPENSE"
    val parentId: String? = null,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)