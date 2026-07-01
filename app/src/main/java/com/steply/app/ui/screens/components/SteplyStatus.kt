package com.steply.app.ui.screens.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@Composable
fun StatusPill(
    recommendationLevel: String,
    modifier: Modifier = Modifier,
) {
    val normalized = recommendationLevel.uppercase()
    val label = when (normalized) {
        "STEADY" -> "Steady"
        "PRACTICE_NEEDED" -> "Practice Needed"
        "RECHECK" -> "Recheck"
        else -> recommendationLevel.ifBlank { "Status" }
    }
    val (background, foreground) = when (normalized) {
        "STEADY" -> Color(0xFFE3F4EA) to Color(0xFF256D45)
        "PRACTICE_NEEDED" -> Color(0xFFFFF0D8) to Color(0xFF855A08)
        "RECHECK" -> Color(0xFFE7EEFB) to Color(0xFF315A9B)
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    StatusChip(
        text = label,
        color = background,
        modifier = modifier,
        contentColor = foreground,
    )
}

@Composable
fun StatusChip(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = color,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = SteplySpacing.ChipHorizontal,
                vertical = SteplySpacing.ChipVertical,
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
