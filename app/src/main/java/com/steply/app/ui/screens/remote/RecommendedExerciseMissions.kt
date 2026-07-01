package com.steply.app.ui.screens.remote

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.steply.app.ui.screens.components.SteplyCard
import kotlinx.coroutines.delay
import org.json.JSONObject

data class RecommendedExerciseMission(
    val id: String,
    val title: String,
    val description: String,
    val safetyNote: String,
    val durationSeconds: Int,
)

data class RecommendedExercisePlan(
    val testLabel: String,
    val recommendationLevel: String,
    val exercises: List<RecommendedExerciseMission>,
)

@Composable
fun RecommendedExerciseMissionList(
    plan: RecommendedExercisePlan,
    checkedMissionIds: Set<String>,
    onToggleMission: (String) -> Unit,
) {
    val completedCount = plan.exercises.count { it.id in checkedMissionIds }
    SteplyCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
        Text(
            text = "Recommended Exercises",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "${plan.testLabel} - ${plan.recommendationLevel} - $completedCount/${plan.exercises.size} done",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    plan.exercises.forEachIndexed { index, mission ->
        MissionCheckCard(
            index = index,
            mission = mission,
            isCompleted = mission.id in checkedMissionIds,
            onDone = { onToggleMission(mission.id) },
        )
    }
}

@Composable
private fun MissionCheckCard(
    index: Int,
    mission: RecommendedExerciseMission,
    isCompleted: Boolean,
    onDone: () -> Unit,
) {
    var isCompleting by remember(mission.id) { mutableStateOf(false) }

    LaunchedEffect(isCompleting, isCompleted) {
        if (isCompleting && !isCompleted) {
            delay(650)
            onDone()
        }
    }

    AnimatedVisibility(
        visible = !isCompleted,
        exit = fadeOut() + shrinkVertically(),
    ) {
        SteplyCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Checkbox(
                    checked = isCompleting || isCompleted,
                    onCheckedChange = { checked ->
                        if (checked && !isCompleting && !isCompleted) {
                            isCompleting = true
                        }
                    },
                    enabled = !isCompleting && !isCompleted,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (isCompleting) {
                        Text(
                            text = "Done",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Text(
                        text = "${index + 1}. ${mission.title}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = mission.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "${(mission.durationSeconds / 60).coerceAtLeast(1)} min - Guided",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = mission.safetyNote,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

fun parseRecommendedExercisePlan(resultJson: String): RecommendedExercisePlan? {
    return runCatching {
        val json = JSONObject(resultJson)
        val recommendations = json.optJSONArray("recommendations") ?: return null
        val exercises = mutableListOf<RecommendedExerciseMission>()
        for (index in 0 until recommendations.length()) {
            val item = recommendations.optJSONObject(index) ?: continue
            val title = item.optString("title").takeIf { it.isNotBlank() } ?: continue
            exercises += RecommendedExerciseMission(
                id = "${json.optString("id", "result")}-$index-$title",
                title = title,
                description = item.optString("description").takeIf { it.isNotBlank() } ?: "Practice this movement gently.",
                safetyNote = item.optString("safetyNote").takeIf { it.isNotBlank() } ?: "Stop if there is pain, dizziness, or discomfort.",
                durationSeconds = item.optInt("durationSeconds", 60),
            )
        }
        if (exercises.isEmpty()) return null
        RecommendedExercisePlan(
            testLabel = json.optString("testLabel").takeIf { it.isNotBlank() }
                ?: json.optString("testType").takeIf { it.isNotBlank() }
                ?: "Movement Check",
            recommendationLevel = json.optString("recommendationLevel").takeIf { it.isNotBlank() }
                ?: "recommended",
            exercises = exercises,
        )
    }.getOrNull()
}
