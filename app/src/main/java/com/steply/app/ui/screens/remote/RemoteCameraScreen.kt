package com.steply.app.ui.screens.remote

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.steply.app.remote.RemoteCameraStreamer
import com.steply.app.AppContainer
import com.steply.app.sync.SteplyWebSessionPayload
import com.steply.app.ui.screens.components.SteplyCard
import com.steply.app.ui.screens.components.SteplyDestructiveButton
import com.steply.app.ui.screens.components.SteplyPrimaryButton
import com.steply.app.ui.screens.components.SteplyScaffold
import com.steply.app.ui.screens.components.SteplyScreenColumn
import com.steply.app.ui.screens.components.SteplySecondaryButton
import kotlinx.coroutines.launch

@Composable
fun RemoteCameraScreen(
    appContainer: AppContainer,
    sessionId: String,
    serverUrl: String,
    onBack: () -> Unit,
    onChangeProfile: () -> Unit,
    onViewHistory: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED,
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted -> hasCameraPermission = granted }

    val session = remember(sessionId, serverUrl) { SteplyWebSessionPayload(sessionId, serverUrl) }
    var streamer by remember(session.webSocketUrl) { mutableStateOf<RemoteCameraStreamer?>(null) }
    var streaming by remember(session.webSocketUrl) { mutableStateOf(false) }
    var statusMessage by remember(session.webSocketUrl) { mutableStateOf("Not streaming to the PC yet.") }
    var cameraMessage by remember { mutableStateOf("Checking camera permission.") }
    var sentFrames by remember { mutableIntStateOf(0) }
    var savedHistoryCount by remember { mutableIntStateOf(0) }
    var latestExercisePlan by remember { mutableStateOf<RecommendedExercisePlan?>(null) }
    var showExerciseMissions by remember { mutableStateOf(false) }
    var checkedMissionIds by remember { mutableStateOf<Set<String>>(emptySet()) }

    fun stopStreaming() {
        streamer?.close()
        streamer = null
        streaming = false
        statusMessage = "PC streaming stopped."
    }

    fun startStreaming() {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
            return
        }
        streamer?.close()
        val newStreamer = RemoteCameraStreamer(
            serverUrl = session.webSocketUrl,
            onStatus = { message -> statusMessage = message },
            onError = { message ->
                statusMessage = message
                streaming = false
                streamer = null
            },
            onFinalResult = { resultJson ->
                parseRecommendedExercisePlan(resultJson)?.let { plan ->
                    latestExercisePlan = plan
                    showExerciseMissions = false
                    checkedMissionIds = emptySet()
                }
                coroutineScope.launch {
                    runCatching {
                        appContainer.movementHistoryRepository.saveFromPcResult(resultJson)
                    }.onSuccess {
                        savedHistoryCount += 1
                        statusMessage = "PC analysis complete. Recommended exercises are ready."
                    }.onFailure { error ->
                        statusMessage = "Failed to save PC result: ${error.message ?: "unknown error"}"
                    }
                }
            },
        )
        streamer = newStreamer
        streaming = true
        sentFrames = 0
        statusMessage = "Connecting to PC Web: ${session.webSocketUrl}"
        newStreamer.connect()
    }

    DisposableEffect(session.webSocketUrl) {
        onDispose { streamer?.close() }
    }

    SteplyScaffold(
        title = "Phone Camera Stream",
        subtitle = "Only camera frames are sent to the PC Web screen.",
        onBack = onBack,
    ) { paddingValues ->
        SteplyScreenColumn(paddingValues = paddingValues) {
            SteplyCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Text(
                    text = "Camera-Only Mode",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Movement analysis runs on the PC. This phone handles profile linking, camera streaming, and local history storage.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            SteplyCard {
                StatusRow(
                    streaming = streaming,
                    statusMessage = statusMessage,
                    sentFrames = sentFrames,
                    savedHistoryCount = savedHistoryCount,
                )
                if (hasCameraPermission) {
                    CameraStreamPreview(
                        remoteCameraStreamer = streamer,
                        onCameraStatus = { cameraMessage = it },
                        onCameraError = { cameraMessage = it },
                        onFrameSent = {
                            if (streaming) sentFrames += 1
                        },
                    )
                    Text(
                        text = cameraMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Text(
                        text = "Camera permission is required. Allow it to start preview and PC streaming.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    SteplyPrimaryButton(
                        text = "Allow Camera Permission",
                        icon = Icons.Default.CameraAlt,
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    )
                }
            }

            if (streaming) {
                SteplyDestructiveButton(
                    text = "Stop Streaming",
                    icon = Icons.Default.Close,
                    onClick = ::stopStreaming,
                )
            } else {
                SteplyPrimaryButton(
                    text = "Start Camera Stream to PC",
                    icon = Icons.Default.PlayArrow,
                    onClick = ::startStreaming,
                )
            }

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

            latestExercisePlan?.let { plan ->
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
        }
    }
}

@Composable
private fun StatusRow(
    streaming: Boolean,
    statusMessage: String,
    sentFrames: Int,
    savedHistoryCount: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (streaming) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = if (streaming) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = if (streaming) "$statusMessage\nSent frames: $sentFrames 쨌 Saved results: $savedHistoryCount" else statusMessage,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

