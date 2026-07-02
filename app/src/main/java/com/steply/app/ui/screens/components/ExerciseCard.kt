package com.steply.app.ui.screens.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale

@Composable
fun ExerciseCard(
    title: String,
    description: String,
    safetyNote: String,
    durationText: String,
    isCompleted: Boolean,
    onMarkCompleted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 1.01f else 1f,
        animationSpec = tween(220),
        label = "exercise-complete-scale",
    )

    SteplyCard(
        modifier = modifier.scale(scale),
        containerColor = if (isCompleted) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SteplySpacing.SmallGap),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                StatusChip(text = durationText, color = MaterialTheme.colorScheme.secondaryContainer)
            }
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(SteplySizes.IconLarge),
                )
            }
        }
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        WarmNoteSurface(
            text = safetyNote,
            title = "Safety note",
            icon = Icons.Default.Warning,
        )
        SteplyPrimaryButton(
            text = if (isCompleted) "Completed" else "Done",
            icon = Icons.Default.CheckCircle,
            enabled = !isCompleted,
            onClick = onMarkCompleted,
        )
    }
}
