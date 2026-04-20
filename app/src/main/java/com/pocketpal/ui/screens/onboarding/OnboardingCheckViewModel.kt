package com.pocketpal.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketpal.data.PreferencesManager
import com.pocketpal.data.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingCheckState(
    val hasAccounts: Boolean = true,
    val isLoading: Boolean = true
)

@HiltViewModel
class OnboardingCheckViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingCheckState())
    val state: StateFlow<OnboardingCheckState> = _state.asStateFlow()

    init {
        checkAccounts()
    }

    private fun checkAccounts() {
        viewModelScope.launch {
            try {
                // First check preferences
                val prefsHasAccounts = preferencesManager.hasAccounts.first()
                
                if (!prefsHasAccounts) {
                    // Check if any accounts exist in database
                    val accounts = accountRepository.getAllAccounts().first()
                    val hasAccountsInDb = accounts.isNotEmpty()
                    
                    if (hasAccountsInDb) {
                        // Update preferences
                        preferencesManager.setHasAccounts(true)
                    }
                    
                    _state.value = OnboardingCheckState(
                        hasAccounts = hasAccountsInDb,
                        isLoading = false
                    )
                } else {
                    _state.value = OnboardingCheckState(
                        hasAccounts = true,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.value = OnboardingCheckState(
                    hasAccounts = false,
                    isLoading = false
                )
            }
        }
    }
}