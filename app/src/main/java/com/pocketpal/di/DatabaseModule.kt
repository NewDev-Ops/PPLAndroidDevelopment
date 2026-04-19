package com.pocketpal.di

import android.content.Context
import com.pocketpal.data.local.AccountDao
import com.pocketpal.data.local.CategoryDao
import com.pocketpal.data.local.PocketPalDatabase
import com.pocketpal.data.local.TransactionDao
import com.pocketpal.data.repository.AccountRepository
import com.pocketpal.data.repository.CategoryRepository
import com.pocketpal.data.repository.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePocketPalDatabase(
        @ApplicationContext context: Context
    ): PocketPalDatabase {
        return PocketPalDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideTransactionDao(database: PocketPalDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    @Singleton
    fun provideAccountDao(database: PocketPalDatabase): AccountDao {
        return database.accountDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: PocketPalDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(
        database: PocketPalDatabase
    ): TransactionRepository {
        return TransactionRepository(database)
    }

    @Provides
    @Singleton
    fun provideAccountRepository(
        database: PocketPalDatabase
    ): AccountRepository {
        return AccountRepository(database)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(
        database: PocketPalDatabase
    ): CategoryRepository {
        return CategoryRepository(database)
    }
}