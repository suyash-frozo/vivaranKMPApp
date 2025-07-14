package com.vivaran.shared.data.repository

import com.vivaran.shared.data.network.OAuthService
import com.vivaran.shared.domain.model.User
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface EnhancedAuthRepository {
    val currentUser: Flow<User?>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User>
    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signInWithGitHub(code: String): Result<User>
    suspend fun signOut()
    suspend fun getCurrentUser(): User?
    suspend fun refreshUserStatus(): Result<User?>
}

class EnhancedAuthRepositoryImpl(
    private val firebaseAuthRepository: AuthRepository,
    private val oauthService: OAuthService,
    private val settings: Settings
) : EnhancedAuthRepository {
    
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUser.asStateFlow()
    
    private companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_EMAIL = "user_email"
        const val KEY_USER_NAME = "user_name"
        const val KEY_AUTH_PROVIDER = "auth_provider"
    }
    
    init {
        // Check for existing OAuth session
        val token = settings.getStringOrNull(KEY_ACCESS_TOKEN)
        if (token != null) {
            val user = getUserFromSettings()
            _currentUser.value = user
        }
    }
    
    override suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User> {
        return firebaseAuthRepository.signInWithEmailAndPassword(email, password)
            .also { result ->
                result.onSuccess { user ->
                    _currentUser.value = user
                    saveUserToSettings(user, "firebase")
                }
            }
    }
    
    override suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<User> {
        return firebaseAuthRepository.createUserWithEmailAndPassword(email, password)
            .also { result ->
                result.onSuccess { user ->
                    _currentUser.value = user
                    saveUserToSettings(user, "firebase")
                }
            }
    }
    
    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val oauthResponse = oauthService.loginWithGoogle(idToken)
            val user = User(
                id = oauthResponse.user.id,
                email = oauthResponse.user.email,
                displayName = oauthResponse.user.name,
                photoUrl = oauthResponse.user.avatar_url
            )
            
            settings.putString(KEY_ACCESS_TOKEN, oauthResponse.access_token)
            saveUserToSettings(user, "google")
            _currentUser.value = user
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun signInWithGitHub(code: String): Result<User> {
        return try {
            val oauthResponse = oauthService.loginWithGitHub(code)
            val user = User(
                id = oauthResponse.user.id,
                email = oauthResponse.user.email,
                displayName = oauthResponse.user.name,
                photoUrl = oauthResponse.user.avatar_url
            )
            
            settings.putString(KEY_ACCESS_TOKEN, oauthResponse.access_token)
            saveUserToSettings(user, "github")
            _currentUser.value = user
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun signOut() {
        val authProvider = settings.getStringOrNull(KEY_AUTH_PROVIDER)
        
        if (authProvider == "firebase") {
            firebaseAuthRepository.signOut()
        } else {
            // OAuth logout
            val token = settings.getStringOrNull(KEY_ACCESS_TOKEN)
            if (token != null) {
                try {
                    oauthService.logout(token)
                } catch (e: Exception) {
                    // Log error but continue with local logout
                }
            }
        }
        
        clearUserFromSettings()
        _currentUser.value = null
    }
    
    override suspend fun getCurrentUser(): User? {
        return _currentUser.value
    }
    
    override suspend fun refreshUserStatus(): Result<User?> {
        val token = settings.getStringOrNull(KEY_ACCESS_TOKEN)
        if (token != null) {
            return try {
                val statusResponse = oauthService.getUserStatus(token)
                if (statusResponse.authenticated && statusResponse.user != null) {
                    val user = User(
                        id = statusResponse.user.id,
                        email = statusResponse.user.email,
                        displayName = statusResponse.user.name,
                        photoUrl = statusResponse.user.avatar_url
                    )
                    _currentUser.value = user
                    Result.success(user)
                } else {
                    signOut()
                    Result.success(null)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
        
        return Result.success(_currentUser.value)
    }
    
    private fun saveUserToSettings(user: User, provider: String) {
        settings.putString(KEY_USER_ID, user.id)
        settings.putString(KEY_USER_EMAIL, user.email)
        settings.putString(KEY_USER_NAME, user.displayName ?: "")
        settings.putString(KEY_AUTH_PROVIDER, provider)
    }
    
    private fun getUserFromSettings(): User? {
        val id = settings.getStringOrNull(KEY_USER_ID) ?: return null
        val email = settings.getStringOrNull(KEY_USER_EMAIL) ?: return null
        val name = settings.getStringOrNull(KEY_USER_NAME)
        
        return User(
            id = id,
            email = email,
            displayName = name,
            photoUrl = null
        )
    }
    
    private fun clearUserFromSettings() {
        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_USER_ID)
        settings.remove(KEY_USER_EMAIL)
        settings.remove(KEY_USER_NAME)
        settings.remove(KEY_AUTH_PROVIDER)
    }
}