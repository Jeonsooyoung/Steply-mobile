package com.steply.app.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun ProfileAvatar(
    displayName: String,
    modifier: Modifier = Modifier,
) {
    val initials = displayName
        .trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifBlank { "S" }

    Box(
        modifier = modifier
            .size(SteplySizes.Avatar)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(MaterialTheme.colorScheme.primary, SteplyAccentOrange),
                ),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
        )
    }
}
