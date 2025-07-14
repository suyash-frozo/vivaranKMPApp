package com.vivaran.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Document(
    val id: String,
    val fileName: String,
    val fileType: FileType,
    val fileSize: Long,
    val uploadedAt: Long,
    val userId: String,
    val sessionId: String,
    val agentId: String? = null,
    val status: DocumentStatus = DocumentStatus.UPLOADING,
    val downloadUrl: String? = null
)

@Serializable
enum class FileType {
    PDF, JPEG, PNG
}

@Serializable
enum class DocumentStatus {
    UPLOADING,
    PROCESSING,
    COMPLETED,
    FAILED
}