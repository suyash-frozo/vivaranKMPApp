package com.vivaran.shared.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

interface OAuthService {
    suspend fun loginWithGoogle(idToken: String): OAuthResponse
    suspend fun loginWithGitHub(code: String): OAuthResponse
    suspend fun getUserStatus(token: String): UserStatusResponse
    suspend fun logout(token: String): LogoutResponse
}

class OAuthServiceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://endearing-prosperity-production.up.railway.app"
) : OAuthService {
    
    override suspend fun loginWithGoogle(idToken: String): OAuthResponse {
        return httpClient.post("$baseUrl/auth/google") {
            contentType(ContentType.Application.Json)
            setBody(GoogleLoginRequest(idToken))
        }.body()
    }
    
    override suspend fun loginWithGitHub(code: String): OAuthResponse {
        return httpClient.post("$baseUrl/auth/github") {
            contentType(ContentType.Application.Json)
            setBody(GitHubLoginRequest(code))
        }.body()
    }
    
    override suspend fun getUserStatus(token: String): UserStatusResponse {
        return httpClient.get("$baseUrl/auth/user") {
            bearerAuth(token)
        }.body()
    }
    
    override suspend fun logout(token: String): LogoutResponse {
        return httpClient.post("$baseUrl/auth/logout") {
            bearerAuth(token)
        }.body()
    }
}

@Serializable
data class GoogleLoginRequest(
    val id_token: String
)

@Serializable
data class GitHubLoginRequest(
    val code: String
)

@Serializable
data class OAuthResponse(
    val access_token: String,
    val user: OAuthUser,
    val expires_in: Long? = null
)

@Serializable
data class OAuthUser(
    val id: String,
    val email: String,
    val name: String,
    val avatar_url: String? = null,
    val provider: String
)

@Serializable
data class UserStatusResponse(
    val authenticated: Boolean,
    val user: OAuthUser? = null
)

@Serializable
data class LogoutResponse(
    val success: Boolean,
    val message: String
)