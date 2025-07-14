package com.vivaran.app.presentation.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benasher44.uuid.uuid4
import com.vivaran.shared.data.network.AnalysisRequest
import com.vivaran.shared.data.network.EnhancedAnalysisRequest
import com.vivaran.shared.data.network.VivaranApiService
import com.vivaran.shared.data.repository.SessionRepository
import com.vivaran.shared.domain.model.AnalysisResult
import com.vivaran.shared.domain.model.AnalysisSession
import com.vivaran.shared.domain.model.Document
import com.vivaran.shared.domain.model.DocumentStatus
import com.vivaran.shared.domain.model.FileType
import com.vivaran.shared.utils.Base64Utils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

data class UploadState(
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f,
    val currentSession: AnalysisSession? = null,
    val analysisResult: AnalysisResult? = null,
    val error: String? = null,
    val uploadSuccess: Boolean = false
)

class UploadViewModel(
    private val sessionRepository: SessionRepository,
    private val apiService: VivaranApiService
) : ViewModel() {
    
    private val _uploadState = MutableStateFlow(UploadState())
    val uploadState: StateFlow<UploadState> = _uploadState.asStateFlow()
    
    fun uploadAndAnalyzeDocument(
        userId: String,
        agentId: String?,
        fileName: String,
        fileData: ByteArray,
        fileType: FileType,
        stateCode: String = "DL",
        insuranceType: String = "cghs",
        language: String = "english"
    ) {
        viewModelScope.launch {
            try {
                _uploadState.value = _uploadState.value.copy(isUploading = true, error = null)
                
                // Create new session
                val session = sessionRepository.createSession(
                    userId = userId,
                    title = "Analysis - $fileName",
                    agentId = agentId
                )
                
                _uploadState.value = _uploadState.value.copy(
                    currentSession = session,
                    uploadProgress = 0.2f
                )
                
                // Create document record
                val documentId = uuid4().toString()
                val document = Document(
                    id = documentId,
                    fileName = fileName,
                    fileType = fileType,
                    fileSize = fileData.size.toLong(),
                    uploadedAt = Clock.System.now().toEpochMilliseconds(),
                    userId = userId,
                    sessionId = session.id,
                    agentId = agentId,
                    status = DocumentStatus.PROCESSING
                )
                
                // Update session with document
                val updatedSession = session.copy(documents = listOf(document))
                sessionRepository.updateSession(updatedSession)
                
                _uploadState.value = _uploadState.value.copy(
                    currentSession = updatedSession,
                    uploadProgress = 0.4f
                )
                
                // Encode file to base64
                val base64Content = Base64Utils.encodeToString(fileData)
                val fileFormat = Base64Utils.getFileFormat(fileName)
                
                _uploadState.value = _uploadState.value.copy(uploadProgress = 0.6f)
                
                // Choose appropriate analysis based on agent
                val apiResponse = if (agentId != null) {
                    // Use enhanced analysis for specific agents
                    val request = EnhancedAnalysisRequest(
                        file_content = base64Content,
                        doc_id = documentId,
                        user_id = userId,
                        language = language,
                        state_code = stateCode,
                        insurance_type = insuranceType,
                        file_format = fileFormat,
                        bypass_router = false
                    )
                    apiService.analyzeDocumentEnhanced(request)
                } else {
                    // Use standard analysis
                    val request = AnalysisRequest(
                        file_content = base64Content,
                        doc_id = documentId,
                        user_id = userId,
                        language = language,
                        state_code = stateCode,
                        insurance_type = insuranceType,
                        file_format = fileFormat
                    )
                    apiService.analyzeDocument(request)
                }
                
                _uploadState.value = _uploadState.value.copy(uploadProgress = 0.8f)
                
                if (apiResponse != null) {
                    // Create analysis result from API response
                    val analysisResult = if (apiResponse is com.vivaran.shared.data.network.EnhancedAnalysisResponse) {
                        AnalysisResult(
                            id = uuid4().toString(),
                            sessionId = session.id,
                            documentId = documentId,
                            verdict = apiResponse.final_result?.verdict ?: "Analysis completed",
                            summary = apiResponse.final_result?.verdict ?: "Document analyzed successfully",
                            insights = extractInsights(apiResponse.final_result?.detailed_analysis),
                            recommendations = extractRecommendations(apiResponse.final_result?.total_overcharge),
                            confidence = apiResponse.final_result?.confidence_score ?: 0.0,
                            billAmount = apiResponse.final_result?.bill_amount,
                            totalOvercharge = apiResponse.final_result?.total_overcharge,
                            processingTime = apiResponse.processing_time,
                            processedAt = Clock.System.now().toEpochMilliseconds()
                        )
                    } else if (apiResponse is com.vivaran.shared.data.network.AnalysisResponse) {
                        AnalysisResult(
                            id = uuid4().toString(),
                            sessionId = session.id,
                            documentId = documentId,
                            verdict = apiResponse.verdict ?: "Analysis completed",
                            summary = apiResponse.verdict ?: "Document analyzed successfully",
                            insights = extractInsights(null),
                            recommendations = extractRecommendations(apiResponse.total_overcharge),
                            confidence = apiResponse.confidence_score ?: 0.0,
                            billAmount = apiResponse.bill_amount,
                            totalOvercharge = apiResponse.total_overcharge,
                            processingTime = apiResponse.processing_time,
                            processedAt = Clock.System.now().toEpochMilliseconds()
                        )
                    } else {
                        throw Exception("Unknown response type")
                    }
                    
                    // Save result to session
                    sessionRepository.addResultToSession(session.id, analysisResult)
                    
                    _uploadState.value = _uploadState.value.copy(
                        uploadProgress = 1f,
                        uploadSuccess = true,
                        isUploading = false,
                        analysisResult = analysisResult
                    )
                } else {
                    throw Exception("Analysis failed")
                }
                
            } catch (e: Exception) {
                _uploadState.value = _uploadState.value.copy(
                    isUploading = false,
                    error = e.message ?: "Upload and analysis failed"
                )
            }
        }
    }
    
    private fun extractInsights(detailedAnalysis: Map<String, String>?): List<String> {
        return detailedAnalysis?.values?.toList() ?: emptyList()
    }
    
    private fun extractRecommendations(overcharge: Double?): List<String> {
        val recommendations = mutableListOf<String>()
        
        if (overcharge != null && overcharge > 0) {
            recommendations.add("Potential overcharge of ₹${overcharge} detected")
            recommendations.add("Review itemized charges for accuracy")
            recommendations.add("Consider discussing discrepancies with healthcare provider")
        } else {
            recommendations.add("Bill appears to be accurate with no significant overcharges")
            recommendations.add("Keep receipt for insurance claims")
        }
        
        return recommendations
    }
    
    fun clearError() {
        _uploadState.value = _uploadState.value.copy(error = null)
    }
    
    fun resetUpload() {
        _uploadState.value = UploadState()
    }
}