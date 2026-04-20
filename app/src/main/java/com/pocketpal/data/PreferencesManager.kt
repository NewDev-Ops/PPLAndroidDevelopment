package com.pocketpal.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pocketpal_prefs")

@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private val HAS_ACCOUNTS = booleanPreferencesKey("has_accounts")
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val hasAccounts: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[HAS_ACCOUNTS] ?: false
        }

    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE] ?: false
        }

    suspend fun setHasAccounts(hasAccounts: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HAS_ACCOUNTS] = hasAccounts
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE] = enabled
        }
    }
}