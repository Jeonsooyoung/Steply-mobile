package com.steply.app.ui.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun SteplyScaffold(
    title: String? = null,
    onBack: (() -> Unit)? = null,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (title != null || onBack != null) {
                SteplyTopBar(
                    title = title.orEmpty(),
                    subtitle = subtitle,
                    onBack = onBack,
                    actions = actions,
                )
            }
        },
        bottomBar = bottomBar,
        content = content,
    )
}

enum class SteplyMainTab {
    Today,
    Check,
    History,
    Settings,
}

@Composable
fun SteplyBottomNavigation(
    currentTab: SteplyMainTab,
    onToday: () -> Unit,
    onCheck: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
) {
    val compactHeight = LocalConfiguration.current.screenHeightDp < 500

    NavigationBar(
        modifier = if (compactHeight) Modifier.height(64.dp) else Modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
    ) {
        SteplyBottomNavigationItem(
            selected = currentTab == SteplyMainTab.Today,
            label = "Today",
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
            onClick = onToday,
            showLabel = !compactHeight,
        )
        SteplyBottomNavigationItem(
            selected = currentTab == SteplyMainTab.Check,
            label = "Check",
            icon = { Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = null) },
            onClick = onCheck,
            showLabel = !compactHeight,
        )
        SteplyBottomNavigationItem(
            selected = currentTab == SteplyMainTab.History,
            label = "History",
            icon = { Icon(imageVector = Icons.Default.History, contentDescription = null) },
            onClick = onHistory,
            showLabel = !compactHeight,
        )
        SteplyBottomNavigationItem(
            selected = currentTab == SteplyMainTab.Settings,
            label = "Settings",
            icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = null) },
            onClick = onSettings,
            showLabel = !compactHeight,
        )
    }
}

@Composable
private fun RowScope.SteplyBottomNavigationItem(
    selected: Boolean,
    label: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    showLabel: Boolean = true,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = icon,
        label = if (showLabel) {
            {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
            )
            }
        } else {
            null
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
}

@Composable
fun SteplyTopBar(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Surface(
        modifier = Modifier.statusBarsPadding(),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = SteplySpacing.TopBarHorizontal,
                    vertical = SteplySpacing.TopBarVertical,
                ),
            horizontalArrangement = Arrangement.spacedBy(SteplySpacing.MediumGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(SteplySizes.TopBarButton)
                        .background(MaterialTheme.colorScheme.surface, CircleShape),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SteplySpacing.ExtraSmallGap),
            ) {
                if (title.isNotBlank()) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Row(content = actions)
        }
    }
}

@Composable
fun SteplyScreenColumn(
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(320)),
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = SteplySizes.ScreenMaxWidth)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = SteplySpacing.ScreenHorizontal,
                        top = SteplySpacing.ScreenVertical,
                        end = SteplySpacing.ScreenHorizontal,
                        bottom = SteplySpacing.ScreenVertical + 72.dp,
                    ),
                verticalArrangement = Arrangement.spacedBy(SteplySpacing.SectionGap),
            ) {
                content()
            }
        }
    }
}
