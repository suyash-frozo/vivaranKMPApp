package com.vivaran.shared.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.Serializable

interface VivaranApiService {
    suspend fun analyzeDocument(request: AnalysisRequest): AnalysisResponse
    suspend fun analyzeDocumentEnhanced(request: EnhancedAnalysisRequest): EnhancedAnalysisResponse
    suspend fun getHealthStatus(): HealthResponse
    suspend fun getAvailableAgents(): List<AgentInfo>
}

class VivaranApiServiceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "https://endearing-prosperity-production.up.railway.app"
) : VivaranApiService {

    override suspend fun analyzeDocument(request: AnalysisRequest): AnalysisResponse {
        return httpClient.post("$baseUrl/analyze") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun analyzeDocumentEnhanced(request: EnhancedAnalysisRequest): EnhancedAnalysisResponse {
        return httpClient.post("$baseUrl/analyze-enhanced") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getHealthStatus(): HealthResponse {
        return httpClient.get("$baseUrl/health").body()
    }

    override suspend fun getAvailableAgents(): List<AgentInfo> {
        return httpClient.get("$baseUrl/agents").body()
    }
}

// Request Models
@Serializable
data class AnalysisRequest(
    val file_content: String, // Base64 encoded document
    val doc_id: String,
    val user_id: String,
    val language: String = "english",
    val state_code: String = "DL", // Default to Delhi
    val insurance_type: String = "cghs", // Default to CGHS
    val file_format: String = "pdf"
)

@Serializable
data class EnhancedAnalysisRequest(
    val file_content: String,
    val doc_id: String,
    val user_id: String,
    val language: String = "english",
    val state_code: String = "DL",
    val insurance_type: String = "cghs",
    val file_format: String = "pdf",
    val bypass_router: Boolean = false
)

// Response Models
@Serializable
data class AnalysisResponse(
    val success: Boolean,
    val verdict: String? = null,
    val total_overcharge: Double? = null,
    val confidence_score: Double? = null,
    val bill_amount: Double? = null,
    val processing_time: Double? = null,
    val error: String? = null
)

@Serializable
data class EnhancedAnalysisResponse(
    val success: Boolean,
    val doc_id: String,
    val processing_stages: ProcessingStages? = null,
    val final_result: FinalResult? = null,
    val processing_time: Double? = null,
    val error: String? = null
)

@Serializable
data class ProcessingStages(
    val router_decision: String? = null,
    val agent_used: String? = null,
    val confidence_threshold: Double? = null
)

@Serializable
data class FinalResult(
    val verdict: String,
    val total_overcharge: Double? = null,
    val confidence_score: Double,
    val bill_amount: Double? = null,
    val detailed_analysis: Map<String, String>? = null
)

@Serializable
data class HealthResponse(
    val status: String,
    val timestamp: String,
    val uptime: Double? = null
)

@Serializable
data class AgentInfo(
    val name: String,
    val description: String,
    val capabilities: List<String>,
    val status: String
)