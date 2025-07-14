package com.vivaran.shared.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Agent(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val category: AgentCategory
)

@Serializable
enum class AgentCategory {
    MEDICAL_ANALYSIS,
    INSURANCE_OPTIMIZATION,
    FARMER_SCHEMES,
    GENERAL_DOCUMENT
}

object AgentFactory {
    fun getDefaultAgents(): List<Agent> = listOf(
        Agent(
            id = "medical_analysis",
            name = "📄 Medical Report Analysis",
            description = "Analyze medical bills, prescriptions, and health reports",
            icon = "📄",
            category = AgentCategory.MEDICAL_ANALYSIS
        ),
        Agent(
            id = "insurance_optimization",
            name = "🛡️ Insurance Claim Optimization", 
            description = "Optimize insurance claims and coverage analysis",
            icon = "🛡️",
            category = AgentCategory.INSURANCE_OPTIMIZATION
        ),
        Agent(
            id = "farmer_schemes",
            name = "🌾 Farmer Scheme Eligibility",
            description = "Check eligibility for Indian government farmer schemes",
            icon = "🌾",
            category = AgentCategory.FARMER_SCHEMES
        ),
        Agent(
            id = "general_document",
            name = "📊 General Document Summarizer",
            description = "Summarize and analyze any document type",
            icon = "📊",
            category = AgentCategory.GENERAL_DOCUMENT
        )
    )
}