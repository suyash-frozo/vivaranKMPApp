package com.vivaran.shared.data.repository

import com.vivaran.shared.domain.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockAuthRepository : AuthRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUser.asStateFlow()
    
    override suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User> {
        delay(1000) // Simulate network delay
        
        // Mock successful login
        val mockUser = User(
            id = "mock_user_123",
            email = email,
            displayName = "Demo User",
            createdAt = 1640995200000L // Some timestamp
        )
        
        _currentUser.value = mockUser
        return Result.success(mockUser)
    }
    
    override suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<User> {
        delay(1000) // Simulate network delay
        
        // Mock successful signup
        val mockUser = User(
            id = "mock_user_${System.currentTimeMillis()}",
            email = email,
            displayName = email.substringBefore("@"),
            createdAt = System.currentTimeMillis()
        )
        
        _currentUser.value = mockUser
        return Result.success(mockUser)
    }
    
    override suspend fun signOut() {
        _currentUser.value = null
    }
    
    override suspend fun getCurrentUser(): User? {
        return _currentUser.value
    }
}