package com.vivaran.shared.domain.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.Clock

@Serializable
data class User(
    val id: String,
    val email: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)