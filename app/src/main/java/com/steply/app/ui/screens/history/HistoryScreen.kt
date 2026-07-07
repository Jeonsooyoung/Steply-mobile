package com.steply.app.ui.screens.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
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
import java.util.Locale

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
                val groupedItems = uiState.items.asReversed().groupBy { it.historyTestCategory() }

                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    HistoryOverviewTrends(
                        standingItems = groupedItems[HistoryTestCategory.STANDING].orEmpty(),
                        chairStandItems = groupedItems[HistoryTestCategory.CHAIR_STAND].orEmpty(),
                        tugItems = groupedItems[HistoryTestCategory.TUG].orEmpty(),
                    )
                    HistoryTestSection(
                        title = "Standing",
                        items = groupedItems[HistoryTestCategory.STANDING].orEmpty(),
                    )
                    HistoryTestSection(
                        title = "Chair Stand",
                        items = groupedItems[HistoryTestCategory.CHAIR_STAND].orEmpty(),
                    )
                    HistoryTestSection(
                        title = "TUG",
                        items = groupedItems[HistoryTestCategory.TUG].orEmpty(),
                    )
                    HistoryTestSection(
                        title = "Other",
                        items = groupedItems[HistoryTestCategory.OTHER].orEmpty(),
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryOverviewTrends(
    standingItems: List<MovementHistory>,
    chairStandItems: List<MovementHistory>,
    tugItems: List<MovementHistory>,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Trend overview",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        HistoryTrendCard(category = HistoryTestCategory.STANDING, items = standingItems)
        HistoryTrendCard(category = HistoryTestCategory.CHAIR_STAND, items = chairStandItems)
        HistoryTrendCard(category = HistoryTestCategory.TUG, items = tugItems)
    }
}

@Composable
private fun HistoryTestSection(
    title: String,
    items: List<MovementHistory>,
) {
    if (items.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        items.forEach { item ->
            HistoryItemCard(item = item)
        }
    }
}

@Composable
private fun HistoryTrendCard(
    category: HistoryTestCategory,
    items: List<MovementHistory>,
) {
    val trendPoints = items.mapNotNull { item ->
        item.trendMetricValue(category)?.let { value ->
            HistoryTrendPoint(
                label = item.testType?.toDisplayLabel() ?: formatDisplayDateTime(item.receivedAt),
                value = value,
            )
        }
    }.takeLast(5)

    if (trendPoints.size < 2) return

    val firstValue = trendPoints.first().value
    val latestValue = trendPoints.last().value
    val delta = latestValue - firstValue
    val isImproving = delta > 0
    val isWorsening = delta < 0
    val trendColor = when {
        isImproving -> Color(0xFF1B8F3A)
        isWorsening -> Color(0xFFC62828)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val comparison = items.trendComparison(category)

    SteplyCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = category.displayName(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = category.trendTitle(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "최근 5회 기준",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                TrendBadge(
                    text = when {
                        isImproving -> "+$delta"
                        isWorsening -> delta.toString()
                        else -> "변화 없음"
                    },
                    color = trendColor,
                )
            }

            SparklineChart(
                points = trendPoints,
                lineColor = trendColor,
                improveByIncrease = category.isImprovedByIncrease(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TrendStat(label = "첫 기록", value = firstValue.toString())
                TrendStat(label = "최신", value = latestValue.toString())
                TrendStat(label = "변화", value = if (delta > 0) "+$delta" else delta.toString())
            }

            comparison?.let {
                TrendComparisonBanner(
                    category = category,
                    comparison = it,
                )
            } ?: TrendComparisonFallbackBanner(category = category)
        }
    }
}

@Composable
private fun SparklineChart(
    points: List<HistoryTrendPoint>,
    lineColor: Color,
    improveByIncrease: Boolean,
) {
    val values = points.map { it.value }
    val minValue = values.minOrNull() ?: 0
    val maxValue = values.maxOrNull() ?: 0
    val range = (maxValue - minValue).coerceAtLeast(1)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
    ) {
        val chartWidth = size.width
        val chartHeight = size.height
        val stepX = if (points.size > 1) chartWidth / (points.size - 1) else 0f

        points.zipWithNext().forEachIndexed { index, pair ->
            val previousPoint = pair.first
            val currentPoint = pair.second
            val startX = stepX * index
            val endX = stepX * (index + 1)
            val startY = chartHeight - ((previousPoint.value - minValue).toFloat() / range.toFloat() * chartHeight)
            val endY = chartHeight - ((currentPoint.value - minValue).toFloat() / range.toFloat() * chartHeight)
            val segmentColor = when {
                currentPoint.value > previousPoint.value && improveByIncrease -> Color(0xFF1B8F3A)
                currentPoint.value < previousPoint.value && improveByIncrease -> Color(0xFFC62828)
                else -> lineColor
            }

            drawLine(
                color = segmentColor,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 8f,
                cap = StrokeCap.Round,
            )
        }

        points.forEachIndexed { index, point ->
            val x = stepX * index
            val y = chartHeight - ((point.value - minValue).toFloat() / range.toFloat() * chartHeight)
            drawCircle(
                color = lineColor,
                radius = 9f,
                center = Offset(x, y),
            )
            drawCircle(
                color = Color.White,
                radius = 3f,
                center = Offset(x, y),
            )
        }
    }
}

@Composable
private fun TrendBadge(
    text: String,
    color: Color,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.14f),
        contentColor = color,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = color,
        )
    }
}

@Composable
private fun TrendStat(
    label: String,
    value: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
        )
    }
}

private data class HistoryTrendPoint(
    val label: String,
    val value: Int,
)

private enum class HistoryTestCategory {
    STANDING,
    CHAIR_STAND,
    TUG,
    OTHER,
}

private fun MovementHistory.historyTestCategory(): HistoryTestCategory {
    val normalized = testType
        ?.lowercase()
        ?.replace("_", " ")
        ?.trim()
        .orEmpty()

    return when {
        normalized.contains("standing") -> HistoryTestCategory.STANDING
        normalized.contains("posture") -> HistoryTestCategory.STANDING
        normalized.contains("chair") -> HistoryTestCategory.CHAIR_STAND
        normalized.contains("stand up") -> HistoryTestCategory.CHAIR_STAND
        normalized.contains("tug") -> HistoryTestCategory.TUG
        normalized.contains("timed up and go") -> HistoryTestCategory.TUG
        else -> HistoryTestCategory.OTHER
    }
}

private fun MovementHistory.trendMetricValue(category: HistoryTestCategory): Int? {
    return when (category) {
        HistoryTestCategory.CHAIR_STAND -> repetitionCount ?: score
        HistoryTestCategory.TUG -> score ?: durationSeconds
        HistoryTestCategory.STANDING -> score ?: durationSeconds
        HistoryTestCategory.OTHER -> score ?: repetitionCount ?: durationSeconds
    }
}

private fun HistoryTestCategory.isImprovedByIncrease(): Boolean {
    return true
}

private fun HistoryTestCategory.trendTitle(): String {
    return when (this) {
        HistoryTestCategory.STANDING -> "최근 점수 추세"
        HistoryTestCategory.CHAIR_STAND -> "최근 반복 횟수 추세"
        HistoryTestCategory.TUG -> "최근 점수 추세"
        HistoryTestCategory.OTHER -> "최근 추세"
    }
}

private fun HistoryTestCategory.displayName(): String {
    return when (this) {
        HistoryTestCategory.STANDING -> "Standing"
        HistoryTestCategory.CHAIR_STAND -> "Chair Stand"
        HistoryTestCategory.TUG -> "TUG"
        HistoryTestCategory.OTHER -> "Other"
    }
}

@Composable
private fun TrendComparisonBanner(
    category: HistoryTestCategory,
    comparison: TrendComparison,
) {
    val color = when {
        comparison.delta > 0 -> Color(0xFF1B8F3A)
        comparison.delta < 0 -> Color(0xFFC62828)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val comparisonText = when {
        comparison.delta > 0 -> "최근 3일 평균이 이전 3일보다 좋아졌어요."
        comparison.delta < 0 -> "최근 3일 평균이 이전 3일보다 나빠졌어요."
        else -> "최근 3일 평균이 이전 3일과 같아요."
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.12f),
        contentColor = color,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "${category.displayName()} 3일 평균 비교",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = color,
            )
            Text(
                text = comparisonText,
                style = MaterialTheme.typography.bodyMedium,
                color = color,
            )
            Text(
                text = "최근 3일 평균 ${comparison.recentAverageText()} · 이전 3일 평균 ${comparison.previousAverageText()}",
                style = MaterialTheme.typography.bodySmall,
                color = color,
            )
        }
    }
}

@Composable
private fun TrendComparisonFallbackBanner(category: HistoryTestCategory) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "${category.displayName()} 평균 비교",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "비교할 기록이 아직 부족해요.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private data class TrendComparison(
    val recentAverage: Double,
    val previousAverage: Double,
    val delta: Double,
)

private data class TrendSample(
    val value: Int,
    val receivedAt: Long,
)

private fun List<MovementHistory>.trendComparison(category: HistoryTestCategory): TrendComparison? {
    val samples = mapNotNull { item ->
        item.trendMetricValue(category)?.let { value ->
            TrendSample(
                value = value,
                receivedAt = item.receivedAt,
            )
        }
    }.sortedBy { it.receivedAt }

    if (samples.size < 2) return null

    val comparisonByTime = compareByThreeDayWindows(samples)
    if (comparisonByTime != null) return comparisonByTime

    val recentSamples = samples.takeLast(3)
    val previousSamples = samples.dropLast(3).takeLast(3)
    if (previousSamples.isEmpty()) return null

    val recentAverage = recentSamples.map { it.value.toDouble() }.average()
    val previousAverage = previousSamples.map { it.value.toDouble() }.average()

    return TrendComparison(
        recentAverage = recentAverage,
        previousAverage = previousAverage,
        delta = recentAverage - previousAverage,
    )
}

private fun compareByThreeDayWindows(samples: List<TrendSample>): TrendComparison? {
    val latestReceivedAt = samples.maxOf { it.receivedAt }
    val threeDaysMillis = 3L * 24L * 60L * 60L * 1000L
    val recentStart = latestReceivedAt - threeDaysMillis
    val previousStart = latestReceivedAt - threeDaysMillis * 2L

    val recentValues = samples
        .filter { it.receivedAt >= recentStart }
        .map { it.value.toDouble() }
    val previousValues = samples
        .filter { it.receivedAt >= previousStart && it.receivedAt < recentStart }
        .map { it.value.toDouble() }

    if (recentValues.isEmpty() || previousValues.isEmpty()) return null

    return TrendComparison(
        recentAverage = recentValues.average(),
        previousAverage = previousValues.average(),
        delta = recentValues.average() - previousValues.average(),
    )
}

private fun TrendComparison.recentAverageText(): String {
    return String.format(Locale.US, "%.1f", recentAverage)
}

private fun TrendComparison.previousAverageText(): String {
    return String.format(Locale.US, "%.1f", previousAverage)
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
