package com.pocketpal

import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val type: TransactionType,
    val amount: Double,
    val category: String,
    val accountId: String,
    val toAccountId: String? = null,
    val date: String,
    val note: String = "",
    val recurringId: String? = null
)

enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

data class Account(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: AccountType,
    var balance: Double,
    val color: String = "#3B82F6",
    val currency: String = "USD",
    val icon: String = "wallet",
    val investmentConfig: InvestmentConfig? = null,
    val lastReturnsApplied: String? = null
)

enum class AccountType {
    BANK, CREDIT, CASH, SAVINGS, INVESTMENTS
}

data class InvestmentConfig(
    val returnRate: Double,
    val rateModel: RateModel
)

enum class RateModel {
    ADY, // Annualized Daily Yield
    EAR  // Effective Annual Rate
}

data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val parentId: String? = null,
    val type: CategoryType
)

enum class CategoryType {
    INCOME, EXPENSE, ROOT
}

data class Budget(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val limit: Double,
    val categoryIds: List<String> = emptyList(),
    val isAllCategories: Boolean = false,
    val color: String,
    val icon: String,
    val period: BudgetPeriod
)

enum class BudgetPeriod {
    DAILY, WEEKLY, MONTHLY
}

data class RecurringTransaction(
    val id: String = UUID.randomUUID().toString(),
    val type: TransactionType,
    val amount: Double,
    val category: String,
    val accountId: String,
    val toAccountId: String? = null,
    val interval: RecurringInterval,
    val startDate: String,
    var nextOccurrence: String,
    val note: String = "",
    var isActive: Boolean = true
)

enum class RecurringInterval {
    DAILY, WEEKLY, MONTHLY, YEARLY
}

data class ReminderSettings(
    val enabled: Boolean = true,
    val time: String = "20:00",
    val frequency: ReminderFrequency = ReminderFrequency.DAILY
)

enum class ReminderFrequency {
    DAILY, WEEKLY, MONTHLY
}