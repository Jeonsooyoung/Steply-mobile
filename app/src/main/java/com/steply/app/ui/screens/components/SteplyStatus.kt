package com.steply.app.ui.screens.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

@Composable
fun StatusPill(
    recommendationLevel: String,
    modifier: Modifier = Modifier,
) {
    val normalized = recommendationLevel.trim().replace(' ', '_').uppercase()
    val (background, foreground) = when (normalized) {
        "STEADY" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "PRACTICE_NEEDED" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "RECHECK" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    StatusChip(
        text = formatRecommendationLevelLabel(recommendationLevel),
        color = background,
        modifier = modifier,
        contentColor = foreground,
    )
}

fun formatRecommendationLevelLabel(recommendationLevel: String): String {
    val trimmed = recommendationLevel.trim()
    return when (trimmed.replace(' ', '_').uppercase()) {
        "STEADY" -> "Steady"
        "PRACTICE_NEEDED" -> "Practice needed"
        "RECHECK" -> "Recheck"
        "" -> "Status"
        else -> trimmed
            .replace('_', ' ')
            .lowercase()
            .replaceFirstChar { char -> char.titlecase() }
    }
}

@Composable
fun StatusChip(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Surface(
        modifier = modifier.heightIn(min = SteplySizes.IconLarge),
        shape = CircleShape,
        color = color,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = SteplySpacing.ChipHorizontal,
                vertical = SteplySpacing.ChipVertical,
            ),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            textAlign = TextAlign.Center,
        )
    }
}
