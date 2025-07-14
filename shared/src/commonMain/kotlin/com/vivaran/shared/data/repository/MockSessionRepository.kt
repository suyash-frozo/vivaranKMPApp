package com.vivaran.shared.data.repository

import com.vivaran.shared.domain.model.AnalysisSession
import com.vivaran.shared.domain.model.AnalysisResult
import com.vivaran.shared.domain.model.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock

class MockSessionRepository : SessionRepository {
    
    override suspend fun createSession(userId: String, title: String, agentId: String?): AnalysisSession {
        val mockSession = AnalysisSession(
            id = "mock_session_${System.currentTimeMillis()}",
            userId = userId,
            agentId = agentId,
            title = title,
            status = SessionStatus.ACTIVE,
            documents = emptyList(),
            createdAt = Clock.System.now().toEpochMilliseconds(),
            lastModified = Clock.System.now().toEpochMilliseconds()
        )
        
        return mockSession
    }
    
    override suspend fun getSession(sessionId: String): AnalysisSession? {
        // Return null for mock implementation
        return null
    }
    
    override suspend fun getSessionsByUser(userId: String): Flow<List<AnalysisSession>> {
        // Return empty list for mock implementation
        return flowOf(emptyList())
    }
    
    override suspend fun updateSession(session: AnalysisSession) {
        // Mock implementation - do nothing
    }
    
    override suspend fun addResultToSession(sessionId: String, result: AnalysisResult) {
        // Mock implementation - do nothing
    }
}