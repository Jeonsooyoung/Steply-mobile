package com.steply.app.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.steply.app.ui.text.SteplyCopy

@Composable
fun SafetyNoticeCard(
    modifier: Modifier = Modifier,
    text: String = SteplyCopy.SafetyReminder,
    title: String = "Safety reminder",
) {
    SteplyCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
    ) {
        NoticeRow(
            text = text,
            title = title,
            icon = Icons.Default.Warning,
            textStyleLarge = true,
        )
    }
}

@Composable
fun WarmNoteSurface(
    text: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: ImageVector = Icons.Default.Info,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(SteplyCorners.Notice),
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    ) {
        NoticeRow(
            text = text,
            title = title,
            icon = icon,
            modifier = Modifier.padding(SteplySpacing.NoticePadding),
        )
    }
}

@Composable
fun LocalDataNoticeCard(
    modifier: Modifier = Modifier,
    text: String = SteplyCopy.LocalDataNotice,
) {
    SteplyCard(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SteplySpacing.MediumGap),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(SteplySizes.IconMedium),
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun NoticeRow(
    text: String,
    title: String?,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    textStyleLarge: Boolean = false,
) {
    val contentColor = LocalContentColor.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(SteplySpacing.MediumGap),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(if (textStyleLarge) SteplySizes.ActionIcon else SteplySizes.IconMedium),
        )
        Column(verticalArrangement = Arrangement.spacedBy(SteplySpacing.ExtraSmallGap)) {
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor,
                )
            }
            Text(
                text = text,
                style = if (textStyleLarge) {
                    MaterialTheme.typography.bodyLarge
                } else {
                    MaterialTheme.typography.bodyMedium
                },
                color = contentColor,
            )
        }
    }
}
