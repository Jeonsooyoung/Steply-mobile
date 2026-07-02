package com.steply.app.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.steply.app.domain.model.MovementHistory
import com.steply.app.ui.screens.components.EmptyStateCard
import com.steply.app.ui.screens.components.StatusChip
import com.steply.app.ui.screens.components.StatusPill
import com.steply.app.ui.screens.components.SteplyCard
import com.steply.app.ui.screens.components.SteplyScaffold
import com.steply.app.ui.screens.components.SteplyScreenColumn
import com.steply.app.ui.screens.components.SteplySpacing
import com.steply.app.util.formatDisplayDateTime

@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    onBack: () -> Unit,
) {
    SteplyScaffold(
        title = "History",
        subtitle = "PC analysis results saved on this phone.",
        onBack = onBack,
    ) { paddingValues ->
        SteplyScreenColumn(paddingValues = paddingValues) {
            if (uiState.items.isEmpty()) {
                EmptyStateCard(
                    title = "No history saved yet",
                    message = "Complete a PC analysis to see saved movement results and recommendations here.",
                    icon = Icons.Default.Refresh,
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    uiState.items.forEach { item ->
                        HistoryItemCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItemCard(item: MovementHistory) {
    SteplyCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = item.profileName ?: "Profile ${item.profileId}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = formatDisplayDateTime(item.receivedAt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item.recommendationLevel?.let {
                StatusPill(recommendationLevel = it)
            } ?: StatusChip(
                text = "Saved",
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            )
        }

        item.message?.takeIf { it.isNotBlank() }?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        } ?: Text(
            text = "Movement check result saved.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            HistoryMetricTile(label = "Score", value = item.score?.toString() ?: "-")
            HistoryMetricTile(label = "Test", value = item.testType?.toDisplayLabel() ?: "Movement check")
            HistoryMetricTile(label = keyMetricLabel(item), value = keyMetricValue(item))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusChip(
                text = item.sessionId?.let { "Session linked" } ?: "Local result",
                color = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            item.durationSeconds?.let { seconds ->
                StatusChip(
                    text = "${seconds}s",
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }

        item.flagsText?.takeIf { it.isNotBlank() }?.let { flags ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            ) {
                Text(
                    text = flags,
                    modifier = Modifier.padding(SteplySpacing.NoticePadding),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
        }
    }
}

@Composable
private fun RowScope.HistoryMetricTile(label: String, value: String) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
            )
        }
    }
}

private fun keyMetricLabel(item: MovementHistory): String {
    return when {
        item.repetitionCount != null -> "Reps"
        item.durationSeconds != null -> "Duration"
        else -> "Metric"
    }
}

private fun keyMetricValue(item: MovementHistory): String {
    return when {
        item.repetitionCount != null -> item.repetitionCount.toString()
        item.durationSeconds != null -> "${item.durationSeconds}s"
        else -> "-"
    }
}

private fun String.toDisplayLabel(): String {
    val trimmed = trim()
    if ('_' !in trimmed && trimmed.any { it.isLowerCase() }) return trimmed

    return trimmed
        .replace('_', ' ')
        .lowercase()
        .replaceFirstChar { char -> char.titlecase() }
}
