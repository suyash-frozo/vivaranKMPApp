package com.vivaran.app.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivaran.shared.data.repository.SessionRepository
import com.vivaran.shared.domain.model.AnalysisSession
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HistoryState(
    val isLoading: Boolean = false,
    val sessions: List<AnalysisSession> = emptyList(),
    val error: String? = null
)

class HistoryViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {
    
    private val _historyState = MutableStateFlow(HistoryState())
    val historyState: StateFlow<HistoryState> = _historyState.asStateFlow()
    
    fun loadUserSessions(userId: String) {
        viewModelScope.launch {
            try {
                _historyState.value = _historyState.value.copy(isLoading = true, error = null)
                
                sessionRepository.getSessionsByUser(userId).collect { sessions ->
                    _historyState.value = _historyState.value.copy(
                        sessions = sessions,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _historyState.value = _historyState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load history"
                )
            }
        }
    }
    
    fun clearError() {
        _historyState.value = _historyState.value.copy(error = null)
    }
}