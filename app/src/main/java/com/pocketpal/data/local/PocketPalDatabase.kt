package com.pocketpal.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TransactionEntity::class,
        AccountEntity::class,
        CategoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PocketPalDatabase : RoomDatabase() {
    
    abstract fun transactionDao(): TransactionDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    
    companion object {
        @Volatile
        private var INSTANCE: PocketPalDatabase? = null
        
        fun getDatabase(context: Context): PocketPalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PocketPalDatabase::class.java,
                    "pocketpal_database"
                )
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
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database)
                }
            }
        }
        
        suspend fun populateDatabase(database: PocketPalDatabase) {
            // Insert default categories
            val defaultCategories = listOf(
                CategoryEntity(
                    id = "cat_food",
                    name = "Food & Drinks",
                    icon = "utensils",
                    color = "#FF5A5F",
                    type = "EXPENSE",
                    isDefault = true
                ),
                CategoryEntity(
                    id = "cat_transport",
                    name = "Transportation",
                    icon = "car",
                    color = "#3B82F6",
                    type = "EXPENSE",
                    isDefault = true
                ),
                CategoryEntity(
                    id = "cat_shopping",
                    name = "Shopping",
                    icon = "shopping-bag",
                    color = "#10B981",
                    type = "EXPENSE",
                    isDefault = true
                ),
                CategoryEntity(
                    id = "cat_entertainment",
                    name = "Entertainment",
                    icon = "film",
                    color = "#8B5CF6",
                    type = "EXPENSE",
                    isDefault = true
                ),
                CategoryEntity(
                    id = "cat_bills",
                    name = "Bills & Utilities",
                    icon = "file-text",
                    color = "#F59E0B",
                    type = "EXPENSE",
                    isDefault = true
                ),
                CategoryEntity(
                    id = "cat_health",
                    name = "Health",
                    icon = "heart",
                    color = "#EF4444",
                    type = "EXPENSE",
                    isDefault = true
                ),
                CategoryEntity(
                    id = "cat_salary",
                    name = "Salary",
                    icon = "briefcase",
                    color = "#10B981",
                    type = "INCOME",
                    isDefault = true
                ),
                CategoryEntity(
                    id = "cat_investment_income",
                    name = "Investment",
                    icon = "trending-up",
                    color = "#3B82F6",
                    type = "INCOME",
                    isDefault = true
                ),
                CategoryEntity(
                    id = "cat_gift",
                    name = "Gift",
                    icon = "gift",
                    color = "#EC4899",
                    type = "INCOME",
                    isDefault = true
                )
            )
            
            database.categoryDao().insertCategories(defaultCategories)
        }
    }
}