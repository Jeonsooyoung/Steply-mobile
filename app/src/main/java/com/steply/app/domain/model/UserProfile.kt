package com.steply.app.domain.model

import java.util.Calendar

data class UserProfile(
    val id: String,
    val displayName: String,
    val birthYear: Int,
    val gender: String?,
    val heightCm: Int?,
    val movementNotes: String?,
    val safetyNote: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val archivedAt: Long?,
) {
    val age: Int
        get() = (Calendar.getInstance().get(Calendar.YEAR) - birthYear).coerceAtLeast(0)
}
