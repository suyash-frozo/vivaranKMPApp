package com.vivaran.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AnalysisSession(
    val id: String,
    val userId: String,
    val title: String,
    val agentId: String? = null,
    val documents: List<Document> = emptyList(),
    val results: List<AnalysisResult> = emptyList(),
    val createdAt: Long,
    val lastModified: Long = createdAt,
    val status: SessionStatus = SessionStatus.ACTIVE
)

@Serializable
data class AnalysisResult(
    val id: String,
    val sessionId: String,
    val documentId: String,
    val verdict: String,
    val summary: String,
    val insights: List<String> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val confidence: Double = 0.0,
    val billAmount: Double? = null,
    val totalOvercharge: Double? = null,
    val processingTime: Double? = null,
    val processedAt: Long,
    val metadata: Map<String, String> = emptyMap()
)

@Serializable
enum class SessionStatus {
    ACTIVE,
    COMPLETED,
    ARCHIVED
}