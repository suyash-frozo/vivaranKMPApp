package com.vivaran.app.navigation

import androidx.compose.runtime.*
// import androidx.lifecycle.viewmodel.compose.viewModel
import com.vivaran.app.presentation.auth.AuthScreen
import com.vivaran.app.presentation.auth.AuthViewModel
import com.vivaran.app.presentation.dashboard.DashboardScreen
import com.vivaran.app.presentation.agent.AgentSelectionScreen
import com.vivaran.app.presentation.upload.UploadScreen
import com.vivaran.app.presentation.result.ResultScreen
import com.vivaran.app.presentation.history.HistoryScreen
import com.vivaran.app.di.getAuthViewModel

sealed class Screen {
    object Auth : Screen()
    object Dashboard : Screen()
    object AgentSelection : Screen()
    object Upload : Screen()
    object Result : Screen()
    object History : Screen()
}

@Composable
fun AppNavigation() {
    val authViewModel: AuthViewModel = getAuthViewModel()
    val authState by authViewModel.authState.collectAsState()
    
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Auth) }
    var selectedAgentId by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            currentScreen = Screen.Dashboard
        } else {
            currentScreen = Screen.Auth
        }
    }
    
    when (currentScreen) {
        Screen.Auth -> {
            AuthScreen(
                onAuthSuccess = { currentScreen = Screen.Dashboard }
            )
        }
        Screen.Dashboard -> {
            DashboardScreen(
                onUploadClick = { currentScreen = Screen.AgentSelection },
                onHistoryClick = { currentScreen = Screen.History },
                onSignOut = { authViewModel.signOut() }
            )
        }
        Screen.AgentSelection -> {
            AgentSelectionScreen(
                onAgentSelected = { agentId ->
                    selectedAgentId = agentId
                    currentScreen = Screen.Upload
                },
                onSkip = {
                    selectedAgentId = null
                    currentScreen = Screen.Upload
                },
                onBack = { currentScreen = Screen.Dashboard }
            )
        }
        Screen.Upload -> {
            UploadScreen(
                selectedAgentId = selectedAgentId,
                onUploadSuccess = { currentScreen = Screen.Result },
                onBack = { currentScreen = Screen.AgentSelection }
            )
        }
        Screen.Result -> {
            ResultScreen(
                onBack = { currentScreen = Screen.Dashboard },
                onNewAnalysis = { currentScreen = Screen.AgentSelection }
            )
        }
        Screen.History -> {
            HistoryScreen(
                onBack = { currentScreen = Screen.Dashboard },
                onSessionClick = { currentScreen = Screen.Result }
            )
        }
    }
}