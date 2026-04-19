package com.pocketpal.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketpal.data.local.CategoryEntity
import com.pocketpal.data.repository.CategoryRepository
import com.pocketpal.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class CategorySpending(
    val categoryName: String,
    val totalSpent: Double,
    val percentage: Float,
    val transactionCount: Int,
    val color: String
)

data class AnalyticsState(
    val totalSpent: Double = 0.0,
    val categorySpending: List<CategorySpending> = emptyList(),
    val currentMonth: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val currentYearMonth = YearMonth.now()
    private val currentMonthStart: String = currentYearMonth.atDay(1).format(DateTimeFormatter.ISO_LOCAL_DATE)
    private val currentMonthEnd: String = currentYearMonth.atEndOfMonth().format(DateTimeFormatter.ISO_LOCAL_DATE)
    
    private val monthLabel = currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))

    val state: StateFlow<AnalyticsState> = combine(
        transactionRepository.getExpensesBetweenDates(currentMonthStart, currentMonthEnd),
        categoryRepository.getAllCategories()
    ) { expenses, categories ->
        // Group expenses by category
        val spendingByCategory = expenses
            .groupBy { it.category }
            .map { (categoryName, categoryExpenses) ->
                val total = categoryExpenses.sumOf { it.amount }
                val category = categories.find { it.name == categoryName }
                CategorySpending(
                    categoryName = categoryName,
                    totalSpent = total,
                    percentage = 0f, // Will calculate after
                    transactionCount = categoryExpenses.size,
                    color = category?.color ?: "#6B7280"
                )
            }
            .sortedByDescending { it.totalSpent }

        // Calculate total spent
        val totalSpent = spendingByCategory.sumOf { it.totalSpent }

        // Calculate percentages
        val spendingWithPercentage = spendingByCategory.map { category ->
            category.copy(
                percentage = if (totalSpent > 0) (category.totalSpent / totalSpent).toFloat() else 0f
            )
        }

        AnalyticsState(
            totalSpent = totalSpent,
            categorySpending = spendingWithPercentage,
            currentMonth = monthLabel,
            isLoading = false
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AnalyticsState(currentMonth = monthLabel)
    )
}