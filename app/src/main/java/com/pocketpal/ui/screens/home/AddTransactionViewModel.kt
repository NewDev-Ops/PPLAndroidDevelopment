package com.pocketpal.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketpal.data.local.CategoryEntity
import com.pocketpal.data.repository.CategoryRepository
import com.pocketpal.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class AddTransactionState(
    val amount: String = "",
    val category: String = "",
    val type: String = "EXPENSE",
    val note: String = "",
    val date: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val accountId: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddTransactionState())
    val state: StateFlow<AddTransactionState> = _state.asStateFlow()

    val categories: StateFlow<List<CategoryEntity>> = categoryRepository
        .getCategoriesByType("EXPENSE")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incomeCategories: StateFlow<List<CategoryEntity>> = categoryRepository
        .getCategoriesByType("INCOME")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateAmount(value: String) {
        _state.value = _state.value.copy(amount = value, errorMessage = null)
    }

    fun updateCategory(category: String) {
        _state.value = _state.value.copy(category = category, errorMessage = null)
    }

    fun updateType(type: String) {
        _state.value = _state.value.copy(type = type, category = "", errorMessage = null)
    }

    fun updateNote(note: String) {
        _state.value = _state.value.copy(note = note)
    }

    fun updateDate(date: String) {
        _state.value = _state.value.copy(date = date)
    }

    fun updateAccountId(accountId: String) {
        _state.value = _state.value.copy(accountId = accountId, errorMessage = null)
    }

    fun saveTransaction(onSuccess: () -> Unit) {
        val currentState = _state.value
        
        // Validation
        if (currentState.amount.isBlank()) {
            _state.value = currentState.copy(errorMessage = "Please enter an amount")
            return
        }
        
        val amount = currentState.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _state.value = currentState.copy(errorMessage = "Please enter a valid amount")
            return
        }
        
        if (currentState.category.isBlank()) {
            _state.value = currentState.copy(errorMessage = "Please select a category")
            return
        }
        
        if (currentState.accountId.isBlank()) {
            _state.value = currentState.copy(errorMessage = "Please select an account")
            return
        }

        viewModelScope.launch {
            _state.value = currentState.copy(isLoading = true, errorMessage = null)
            
            try {
                transactionRepository.addTransaction(
                    type = currentState.type,
                    amount = amount,
                    category = currentState.category,
                    accountId = currentState.accountId,
                    date = currentState.date,
                    note = currentState.note
                )
                
                _state.value = currentState.copy(
                    isLoading = false,
                    isSuccess = true
                )
                onSuccess()
            } catch (e: Exception) {
                _state.value = currentState.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to save transaction"
                )
            }
        }
    }

    fun resetState() {
        _state.value = AddTransactionState()
    }
}