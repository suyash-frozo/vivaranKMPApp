package com.vivaran.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vivaran.shared.data.repository.AuthRepository
import com.vivaran.shared.domain.model.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AuthState(
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _authState.value = _authState.value.copy(
                    isAuthenticated = user != null,
                    user = user,
                    isLoading = false
                )
            }
        }
    }
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            
            authRepository.signInWithEmailAndPassword(email, password)
                .onSuccess { user ->
                    _authState.value = _authState.value.copy(
                        isAuthenticated = true,
                        user = user,
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Sign in failed"
                    )
                }
        }
    }
    
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            
            authRepository.createUserWithEmailAndPassword(email, password)
                .onSuccess { user ->
                    _authState.value = _authState.value.copy(
                        isAuthenticated = true,
                        user = user,
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Sign up failed"
                    )
                }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _authState.value = AuthState()
        }
    }
    
    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}