package com.vivaran.shared.data.repository

import com.benasher44.uuid.uuid4
import com.vivaran.shared.domain.model.AnalysisSession
import com.vivaran.shared.domain.model.AnalysisResult
import com.vivaran.shared.domain.model.SessionStatus
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

interface SessionRepository {
    suspend fun createSession(userId: String, title: String, agentId: String?): AnalysisSession
    suspend fun getSessionsByUser(userId: String): Flow<List<AnalysisSession>>
    suspend fun getSession(sessionId: String): AnalysisSession?
    suspend fun updateSession(session: AnalysisSession)
    suspend fun addResultToSession(sessionId: String, result: AnalysisResult)
}

class SessionRepositoryImpl(
    private val firestore: FirebaseFirestore
) : SessionRepository {
    
    private val sessionsCollection = firestore.collection("sessions")
    
    override suspend fun createSession(userId: String, title: String, agentId: String?): AnalysisSession {
        val session = AnalysisSession(
            id = uuid4().toString(),
            userId = userId,
            title = title,
            agentId = agentId,
            createdAt = Clock.System.now().toEpochMilliseconds()
        )
        
        sessionsCollection.document(session.id).set(session)
        return session
    }

    override suspend fun getSessionsByUser(userId: String): Flow<List<AnalysisSession>> {
        return sessionsCollection
            .where { "userId" equalTo userId }
            .orderBy("lastModified", dev.gitlive.firebase.firestore.Direction.DESCENDING)
            .snapshots
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.data<AnalysisSession>() }
            }
    }

    override suspend fun getSession(sessionId: String): AnalysisSession? {
        return try {
            sessionsCollection.document(sessionId).get().data<AnalysisSession>()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateSession(session: AnalysisSession) {
        val updatedSession = session.copy(lastModified = Clock.System.now().toEpochMilliseconds())
        sessionsCollection.document(session.id).set(updatedSession)
    }

    override suspend fun addResultToSession(sessionId: String, result: AnalysisResult) {
        val session = getSession(sessionId)
        if (session != null) {
            val updatedResults = session.results + result
            val updatedSession = session.copy(
                results = updatedResults,
                lastModified = Clock.System.now().toEpochMilliseconds(),
                status = if (updatedResults.isNotEmpty()) SessionStatus.COMPLETED else SessionStatus.ACTIVE
            )
            updateSession(updatedSession)
        }
    }
}