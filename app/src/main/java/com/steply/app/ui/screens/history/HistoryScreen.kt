package com.steply.app.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.steply.app.domain.model.MovementHistory
import com.steply.app.ui.screens.components.EmptyStateCard
import com.steply.app.ui.screens.components.StatusChip
import com.steply.app.ui.screens.components.SteplyCard
import com.steply.app.ui.screens.components.SteplyScaffold
import com.steply.app.ui.screens.components.SteplyScreenColumn
import com.steply.app.util.formatDisplayDateTime

@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    onBack: () -> Unit,
) {
    SteplyScaffold(
        title = "Movement History",
        subtitle = "Results are saved on this phone after PC analysis finishes.",
        onBack = onBack,
    ) { paddingValues ->
        SteplyScreenColumn(paddingValues = paddingValues) {
            if (uiState.items.isEmpty()) {
                EmptyStateCard(
                    title = "No history saved yet",
                    message = "Link with the PC by QR and complete a movement check to see results here.",
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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
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
            StatusChip(
                text = item.recommendationLevel ?: "Saved",
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            )
        }

        Text(
            text = item.message ?: "Movement check result saved.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HistoryMetric(label = "Reps", value = item.repetitionCount?.toString() ?: "-")
            HistoryMetric(label = "Score", value = item.score?.toString() ?: "-")
            HistoryMetric(label = "Test", value = item.testType ?: "movement")
        }

        item.flagsText?.takeIf { it.isNotBlank() }?.let { flags ->
            Text(
                text = flags,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun RowScope.HistoryMetric(label: String, value: String) {
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
