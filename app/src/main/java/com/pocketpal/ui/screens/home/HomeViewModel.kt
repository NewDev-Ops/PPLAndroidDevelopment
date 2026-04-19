package com.pocketpal.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketpal.data.local.TransactionEntity
import com.pocketpal.data.repository.AccountRepository
import com.pocketpal.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class TransactionGroup(
    val date: String,
    val transactions: List<TransactionEntity>,
    val totalIncome: Double,
    val totalExpense: Double
)

data class HomeState(
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val transactionGroups: List<TransactionGroup> = emptyList(),
    val isLoading: Boolean = true,
    val accounts: List<String> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _accounts = MutableStateFlow<List<String>>(emptyList())

    val state: StateFlow<HomeState> = combine(
        transactionRepository.getAllTransactions(),
        accountRepository.getAllAccounts(),
        _accounts
    ) { transactions, accounts, accountIds ->
        // Get account IDs
        val accountIdList = accounts.map { it.id }
        
        // Calculate totals
        var totalIncome = 0.0
        var totalExpense = 0.0
        
        transactions.forEach { transaction ->
            if (transaction.type == "INCOME") {
                totalIncome += transaction.amount
            } else if (transaction.type == "EXPENSE") {
                totalExpense += transaction.amount
            }
        }

        // Calculate net balance (income - expenses + assets - liabilities)
        val totalAssets = accounts
            .filter { it.type != "CREDIT" }
            .sumOf { it.balance }
        val totalLiabilities = accounts
            .filter { it.type == "CREDIT" }
            .sumOf { it.balance }
        val netBalance = totalAssets - totalLiabilities

        // Group transactions by date
        val grouped = transactions
            .groupBy { it.date.substring(0, 10) } // Group by date (yyyy-MM-dd)
            .map { (date, txns) ->
                val dayIncome = txns.filter { it.type == "INCOME" }.sumOf { it.amount }
                val dayExpense = txns.filter { it.type == "EXPENSE" }.sumOf { it.amount }
                TransactionGroup(
                    date = date,
                    transactions = txns.sortedByDescending { it.createdAt },
                    totalIncome = dayIncome,
                    totalExpense = dayExpense
                )
            }
            .sortedByDescending { it.date }

        // Create default account if none exist
        if (accountIdList.isEmpty()) {
            viewModelScope.launch {
                accountRepository.addAccount(
                    name = "Main Bank",
                    type = "BANK",
                    balance = 0.0
                )
                _accounts.value = listOf("main_bank")
            }
        }

        HomeState(
            totalBalance = netBalance,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            transactionGroups = grouped,
            isLoading = false,
            accounts = accountIdList
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeState()
    )

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            transactionRepository.deleteTransactionById(id)
        }
    }
}