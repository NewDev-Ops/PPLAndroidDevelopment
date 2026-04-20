package com.pocketpal.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pocketpal.data.repository.AccountRepository
import com.pocketpal.data.repository.CategoryRepository
import com.pocketpal.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = false,
    val showClearConfirmDialog: Boolean = false,
    val dataCleared: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun toggleDarkMode() {
        _state.value = _state.value.copy(isDarkMode = !_state.value.isDarkMode)
    }

    fun setDarkMode(enabled: Boolean) {
        _state.value = _state.value.copy(isDarkMode = enabled)
    }

    fun showClearConfirmDialog() {
        _state.value = _state.value.copy(showClearConfirmDialog = true)
    }

    fun hideClearConfirmDialog() {
        _state.value = _state.value.copy(showClearConfirmDialog = false)
    }

    fun clearAllData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                transactionRepository.deleteAllTransactions()
                _state.value = _state.value.copy(
                    isLoading = false,
                    showClearConfirmDialog = false,
                    dataCleared = true
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    showClearConfirmDialog = false
                )
            }
        }
    }

    fun clearDataFlag() {
        _state.value = _state.value.copy(dataCleared = false)
    }
}