package com.pocketpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.pocketpal.navigation.PocketPalNavHost
import com.pocketpal.ui.screens.analytics.AnalyticsViewModel
import com.pocketpal.ui.screens.home.AddTransactionViewModel
import com.pocketpal.ui.screens.home.HomeViewModel
import com.pocketpal.ui.screens.settings.SettingsViewModel
import com.pocketpal.ui.theme.PocketPalTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
            
            PocketPalTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val homeViewModel: HomeViewModel = hiltViewModel()
                    val addTransactionViewModel: AddTransactionViewModel = hiltViewModel()
                    val analyticsViewModel: AnalyticsViewModel = hiltViewModel()
                    val settingsViewModel: SettingsViewModel = hiltViewModel()
                    
                    PocketPalNavHost(
                        addTransactionViewModel = addTransactionViewModel,
                        homeViewModel = homeViewModel,
                        analyticsViewModel = analyticsViewModel,
                        settingsViewModel = settingsViewModel,
                        onDarkModeChange = { isDarkMode = it }
                    )
                }
            }
        }
    }
}