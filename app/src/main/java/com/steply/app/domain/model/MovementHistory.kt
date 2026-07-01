package com.steply.app.domain.model

data class MovementHistory(
    val id: String,
    val profileId: String,
    val profileName: String?,
    val sessionId: String?,
    val testType: String?,
    val score: Int?,
    val repetitionCount: Int?,
    val durationSeconds: Int?,
    val recommendationLevel: String?,
    val message: String?,
    val flagsText: String?,
    val rawJson: String,
    val createdAt: Long,
    val receivedAt: Long,
)
