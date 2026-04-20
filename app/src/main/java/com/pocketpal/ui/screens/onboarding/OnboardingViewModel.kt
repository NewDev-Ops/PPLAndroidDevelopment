package com.pocketpal.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketpal.data.PreferencesManager
import com.pocketpal.data.local.AccountEntity
import com.pocketpal.data.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class AccountType(val displayName: String) {
    CASH("Cash"),
    BANK("Bank Account"),
    MOBILE_MONEY("Mobile Money")
}

data class OnboardingState(
    val accountName: String = "",
    val accountType: String = "BANK",
    val currency: String = "KES",
    val initialBalance: String = "0",
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun updateAccountName(name: String) {
        _state.value = _state.value.copy(accountName = name, errorMessage = null)
    }

    fun updateAccountType(type: String) {
        _state.value = _state.value.copy(accountType = type)
    }

    fun updateCurrency(currency: String) {
        _state.value = _state.value.copy(currency = currency)
    }

    fun updateInitialBalance(balance: String) {
        // Allow only valid decimal numbers
        if (balance.isEmpty() || balance.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _state.value = _state.value.copy(initialBalance = balance, errorMessage = null)
        }
    }

    fun createAccount() {
        val currentState = _state.value

        // Validation
        if (currentState.accountName.isBlank()) {
            _state.value = currentState.copy(errorMessage = "Please enter an account name")
            return
        }

        val balance = currentState.initialBalance.toDoubleOrNull() ?: 0.0

        viewModelScope.launch {
            _state.value = currentState.copy(isLoading = true, errorMessage = null)

            try {
                // Create account in database
                accountRepository.addAccount(
                    name = currentState.accountName,
                    type = currentState.accountType,
                    balance = balance,
                    currency = currentState.currency
                )

                // Mark onboarding as complete
                preferencesManager.setHasAccounts(true)

                _state.value = _state.value.copy(
                    isLoading = false,
                    isComplete = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to create account"
                )
            }
        }
    }
}