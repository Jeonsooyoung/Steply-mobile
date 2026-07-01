package com.steply.app.ui.screens.remote

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.steply.app.domain.model.UserProfile
import androidx.compose.material.icons.filled.CheckCircle
import com.steply.app.ui.screens.components.ProfileAvatar
import com.steply.app.ui.screens.components.RemoteCameraQrScanner
import com.steply.app.ui.screens.components.SteplyCard
import com.steply.app.ui.screens.components.SteplyPrimaryButton
import com.steply.app.ui.screens.components.SteplyScaffold
import com.steply.app.ui.screens.components.SteplyScreenColumn
import com.steply.app.ui.screens.components.SteplySecondaryButton

@Composable
fun RemoteConnectScreen(
    uiState: RemoteConnectUiState,
    pendingRemoteCameraLink: String?,
    onPendingRemoteCameraLinkHandled: () -> Unit,
    onQrScanned: (String) -> Unit,
    onManualQrChanged: (String) -> Unit,
    onConnectManual: () -> Unit,
    onChangeProfile: () -> Unit,
    onAddProfile: () -> Unit,
    onViewHistory: () -> Unit,
) {
    LaunchedEffect(pendingRemoteCameraLink) {
        val value = pendingRemoteCameraLink ?: return@LaunchedEffect
        onQrScanned(value)
        onPendingRemoteCameraLinkHandled()
    }

    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED,
        )
    }
    var isQrScannerOpen by remember { mutableStateOf(false) }
    var showExerciseMissions by remember { mutableStateOf(false) }
    var checkedMissionIds by remember(uiState.latestExercisePlan) { mutableStateOf<Set<String>>(emptySet()) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasCameraPermission = granted
        isQrScannerOpen = granted
    }

    SteplyScaffold(
        title = "Steply Camera Link",
        subtitle = "This phone only stores the profile and streams camera frames.",
    ) { paddingValues ->
        SteplyScreenColumn(paddingValues = paddingValues) {
            SteplyCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Text(
                    text = "Scan the QR code on the PC screen",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Profiles stay on this phone. Only the camera stream is sent to the PC on the same network.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            SelectedProfileCard(
                profile = uiState.selectedProfile,
                onChangeProfile = onChangeProfile,
                onAddProfile = onAddProfile,
                onViewHistory = onViewHistory,
            )

            SteplyCard {
                Text(
                    text = "QR Scan",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Run Steply Web on the PC, then scan the QR code in the left connection panel.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (isQrScannerOpen && hasCameraPermission) {
                    RemoteCameraQrScanner(
                        onQrCodeScanned = { value ->
                            isQrScannerOpen = false
                            onQrScanned(value)
                        },
                    )
                    SteplySecondaryButton(
                        text = "Close QR Scanner",
                        icon = Icons.Default.QrCodeScanner,
                        onClick = { isQrScannerOpen = false },
                    )
                } else {
                    SteplyPrimaryButton(
                        text = if (hasCameraPermission) "Start QR Scan" else "Allow QR Scan Permission",
                        icon = Icons.Default.QrCodeScanner,
                        onClick = {
                            if (hasCameraPermission) {
                                isQrScannerOpen = true
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                    )
                }
            }

            uiState.latestExercisePlan?.let { plan ->
                SteplyPrimaryButton(
                    text = if (showExerciseMissions) "Hide Recommended Exercises" else "Recommended Exercises",
                    icon = Icons.Default.CheckCircle,
                    onClick = { showExerciseMissions = !showExerciseMissions },
                )

                if (showExerciseMissions) {
                    RecommendedExerciseMissionList(
                        plan = plan,
                        checkedMissionIds = checkedMissionIds,
                        onToggleMission = { missionId ->
                            checkedMissionIds = if (missionId in checkedMissionIds) {
                                checkedMissionIds - missionId
                            } else {
                                checkedMissionIds + missionId
                            }
                        },
                    )
                }
            }

            SteplyCard(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                Text(
                    text = "Paste QR Payload Manually",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "If scanning fails, copy the QR payload from the PC dashboard and paste it here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedTextField(
                    value = uiState.manualQrValue,
                    onValueChange = onManualQrChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Steply Web QR payload") },
                    placeholder = { Text("{\"type\":\"steply-web-session\", ...}") },
                    minLines = 3,
                    enabled = !uiState.isConnecting,
                )
                SteplySecondaryButton(
                    text = if (uiState.isConnecting) "Connecting" else "Connect With Pasted Payload",
                    icon = Icons.Default.Refresh,
                    onClick = onConnectManual,
                    enabled = !uiState.isConnecting,
                )
            }

            SteplyCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = uiState.statusMessage,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                uiState.errorMessage?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectedProfileCard(
    profile: UserProfile?,
    onChangeProfile: () -> Unit,
    onAddProfile: () -> Unit,
    onViewHistory: () -> Unit,
) {
    SteplyCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProfileAvatar(displayName = profile?.displayName ?: "?")
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = profile?.displayName ?: "No profile selected",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = if (profile == null) {
                        "Create a profile before linking to the PC."
                    } else {
                        "Birth year ${profile.birthYear}${profile.heightCm?.let { " · ${it} cm" } ?: ""} · This appears on the PC profile lookup panel."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (profile == null) {
            SteplyPrimaryButton(
                text = "Create Profile",
                icon = Icons.Default.Person,
                onClick = onAddProfile,
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SteplySecondaryButton(
                    text = "Change Profile",
                    icon = Icons.Default.Person,
                    onClick = onChangeProfile,
                    modifier = Modifier.weight(1f),
                )
                SteplySecondaryButton(
                    text = "History",
                    icon = Icons.Default.List,
                    onClick = onViewHistory,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
