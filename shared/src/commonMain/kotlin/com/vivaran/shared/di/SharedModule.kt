package com.vivaran.shared.di

import com.vivaran.shared.data.network.VivaranApiService
import com.vivaran.shared.data.network.VivaranApiServiceImpl
import com.vivaran.shared.data.repository.AuthRepository
import com.vivaran.shared.data.repository.AuthRepositoryImpl
import com.vivaran.shared.data.repository.SessionRepository
import com.vivaran.shared.data.repository.SessionRepositoryImpl
import com.vivaran.shared.data.repository.MockAuthRepository
import com.vivaran.shared.data.repository.MockSessionRepository
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

val sharedModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }
    
    // Temporarily disabled Firebase dependencies
    // single { FirebaseAuth.getInstance() }
    // single { FirebaseFirestore.getInstance() }
    single { com.russhwolf.settings.Settings() }
    
    // Use mock repositories instead of Firebase-dependent ones
    single<AuthRepository> { MockAuthRepository() }
    single<SessionRepository> { MockSessionRepository() }
    single<VivaranApiService> { VivaranApiServiceImpl(get()) }
    single<com.vivaran.shared.data.network.OAuthService> { com.vivaran.shared.data.network.OAuthServiceImpl(get()) }
    // Temporarily disabled Enhanced Auth Repository
    // single<com.vivaran.shared.data.repository.EnhancedAuthRepository> { 
    //     com.vivaran.shared.data.repository.EnhancedAuthRepositoryImpl(get(), get(), get()) 
    // }
    
}

expect val platformModule: Module