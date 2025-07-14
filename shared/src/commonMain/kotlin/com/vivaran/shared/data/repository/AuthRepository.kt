package com.vivaran.shared.data.repository

import com.vivaran.shared.domain.model.User
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User>
    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<User>
    suspend fun signOut()
    suspend fun getCurrentUser(): User?
}

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    
    override val currentUser: Flow<User?> = firebaseAuth.authStateChanged.map { firebaseUser ->
        firebaseUser?.toUser()
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password)
            val user = result.user?.toUser()
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to get user data"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
            val user = result.user?.toUser()
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun getCurrentUser(): User? {
        return firebaseAuth.currentUser?.toUser()
    }

    private fun FirebaseUser.toUser(): User {
        return User(
            id = uid,
            email = email ?: "",
            displayName = displayName,
            photoUrl = photoURL
        )
    }
}