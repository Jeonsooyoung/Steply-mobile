package com.steply.app.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.steply.app.domain.model.UserProfile
import com.steply.app.ui.screens.components.EmptyStateCard
import com.steply.app.ui.screens.components.LocalDataNoticeCard
import com.steply.app.ui.screens.components.ProfileAvatar
import com.steply.app.ui.screens.components.SteplySecondaryButton
import com.steply.app.ui.screens.components.StatusChip
import com.steply.app.ui.screens.components.SteplyCard
import com.steply.app.ui.screens.components.SteplyPrimaryButton
import com.steply.app.ui.screens.components.SteplyScaffold
import com.steply.app.ui.screens.components.SteplyScreenColumn

@Composable
fun ProfileListScreen(
    uiState: ProfileListUiState,
    onSelectProfile: (String) -> Unit,
    onAddProfile: () -> Unit,
    onEditProfile: (String) -> Unit,
    onArchiveProfile: (UserProfile) -> Unit,
    onConfirmArchive: () -> Unit,
    onDismissArchive: () -> Unit,
    onBack: (() -> Unit)? = null,
) {
    uiState.archiveTarget?.let {
        AlertDialog(
            onDismissRequest = onDismissArchive,
            title = {
                Text(
                    text = "Hide this profile?",
                    style = MaterialTheme.typography.titleLarge,
                )
            },
            text = {
                Text(
                    text = "This removes the profile from the chooser. Data deletion is handled separately in Settings.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirmArchive,
                    enabled = !uiState.isWorking,
                ) {
                    Text("Hide Profile")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissArchive,
                    enabled = !uiState.isWorking,
                ) {
                    Text("Cancel")
                }
            },
        )
    }

    SteplyScaffold(
        title = "Who is using Steply today?",
        subtitle = "Choose a local profile to continue.",
        onBack = onBack,
    ) { paddingValues ->
        SteplyScreenColumn(paddingValues = paddingValues) {
            LocalDataNoticeCard(
                text = "Each profile keeps its own local movement history on this device.",
            )

            if (uiState.profiles.isEmpty()) {
                EmptyStateCard(
                    title = "Create your first profile",
                    message = "Add a name or nickname so Steply can keep movement checks organized.",
                    icon = Icons.Default.Person,
                    actionText = "Add New Profile",
                    onAction = onAddProfile,
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    uiState.profiles.forEach { profile ->
                        ProfileListItem(
                            profile = profile,
                            selected = profile.id == uiState.selectedUserId,
                            onSelect = { onSelectProfile(profile.id) },
                            onEdit = { onEditProfile(profile.id) },
                            onArchive = { onArchiveProfile(profile) },
                        )
                    }
                }

                SteplyPrimaryButton(
                    text = "Add Profile",
                    icon = Icons.Default.Add,
                    onClick = onAddProfile,
                )
            }

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
private fun ProfileListItem(
    profile: UserProfile,
    selected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
) {
    SteplyCard(
        modifier = Modifier.clickable(onClick = onSelect),
        containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProfileAvatar(displayName = profile.displayName)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = profile.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Birth year ${profile.birthYear}${profile.heightCm?.let { " · ${it} cm" } ?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                StatusChip(
                    text = if (selected) "Selected" else "Tap to continue",
                    color = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                )
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected profile",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SteplySecondaryButton(
                text = "Edit",
                icon = Icons.Default.Edit,
                onClick = onEdit,
                modifier = Modifier.weight(1f),
            )
            SteplySecondaryButton(
                text = "Hide",
                icon = Icons.Default.Person,
                onClick = onArchive,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
