package com.vivaran.app.presentation.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivaran.shared.data.repository.SessionRepository
import com.vivaran.shared.domain.model.AnalysisResult
import com.vivaran.shared.domain.model.AnalysisSession
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ResultState(
    val isLoading: Boolean = false,
    val session: AnalysisSession? = null,
    val results: List<AnalysisResult> = emptyList(),
    val error: String? = null
)

class ResultViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {
    
    private val _resultState = MutableStateFlow(ResultState())
    val resultState: StateFlow<ResultState> = _resultState.asStateFlow()
    
    fun loadSessionResults(sessionId: String) {
        viewModelScope.launch {
            try {
                _resultState.value = _resultState.value.copy(isLoading = true, error = null)
                
                val session = sessionRepository.getSession(sessionId)
                if (session != null) {
                    _resultState.value = _resultState.value.copy(
                        session = session,
                        results = session.results,
                        isLoading = false
                    )
                } else {
                    _resultState.value = _resultState.value.copy(
                        isLoading = false,
                        error = "Session not found"
                    )
                }
            } catch (e: Exception) {
                _resultState.value = _resultState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load results"
                )
            }
        }
    }
    
    fun clearError() {
        _resultState.value = _resultState.value.copy(error = null)
    }
}