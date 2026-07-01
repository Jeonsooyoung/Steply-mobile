package com.steply.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey
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
)
