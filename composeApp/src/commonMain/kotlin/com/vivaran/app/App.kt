package com.vivaran.app

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.vivaran.app.navigation.AppNavigation
import com.vivaran.app.theme.VivaranTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.context.startKoin
import com.vivaran.shared.di.sharedModule
import com.vivaran.app.di.appModule

@Composable
@Preview
fun App() {
    // Initialize Koin once
    LaunchedEffect(Unit) {
        try {
            startKoin {
                modules(sharedModule, appModule)
            }
        } catch (e: Exception) {
            // Koin already started
        }
    }
    
    VivaranTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavigation()
        }
    }
}