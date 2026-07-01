package com.steply.app.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDisplayDateTime(millis: Long): String {
    return SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US).format(Date(millis))
}

fun formatAge(age: Int?): String {
    return age?.let { "$it years" } ?: "Birth year not set"
}

fun formatAverageCount(averageCount: Float): String {
    return String.format(Locale.US, "%.1f", averageCount)
}

fun formatConfidencePercent(confidence: Float): String {
    val percent = (confidence.coerceIn(0f, 1f) * 100).toInt()
    return "$percent%"
}

fun formatDurationShort(durationSeconds: Int): String {
    return if (durationSeconds < 60) {
        "$durationSeconds sec"
    } else {
        "${durationSeconds / 60} min"
    }
}

fun formatChairStandCount(count: Int): String {
    return count.toString()
}

fun formatChairStandUnitLabel(): String {
    return "chair stands"
}

fun formatChairStandDurationLabel(durationSeconds: Int): String {
    return "$durationSeconds-second chair stand"
}
