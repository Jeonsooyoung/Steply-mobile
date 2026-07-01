package com.steply.app.ui.screens.remote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steply.app.AppContainer
import com.steply.app.domain.model.UserProfile
import com.steply.app.sync.SteplyWebClient
import com.steply.app.sync.SteplyWebSessionLink
import com.steply.app.sync.SteplyWebSessionPayload
import com.steply.app.ui.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val ManualSessionId = "manual-camera"

data class RemoteConnectUiState(
    val selectedProfile: UserProfile? = null,
    val manualQrValue: String = "",
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val statusMessage: String = "Scan the QR code on the PC screen to link this profile and camera.",
    val linkedSession: SteplyWebSessionPayload? = null,
    val latestExercisePlan: RecommendedExercisePlan? = null,
)

private data class RemoteConnectActionState(
    val manualQrValue: String = "",
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val statusMessage: String = "Scan the QR code on the PC screen to link this profile and camera.",
    val linkedSession: SteplyWebSessionPayload? = null,
)

class RemoteConnectViewModel(
    private val appContainer: AppContainer,
    private val webClient: SteplyWebClient = SteplyWebClient(),
) : ViewModel() {
    private val actionState = MutableStateFlow(RemoteConnectActionState())

    val uiState: StateFlow<RemoteConnectUiState> = combine(
        appContainer.userProfileRepository.observeActiveProfiles(),
        appContainer.settingsRepository.selectedUserId,
        actionState,
        appContainer.movementHistoryRepository.observeAll(),
    ) { profiles, selectedUserId, action, historyItems ->
        RemoteConnectUiState(
            selectedProfile = profiles.firstOrNull { it.id == selectedUserId },
            manualQrValue = action.manualQrValue,
            isConnecting = action.isConnecting,
            errorMessage = action.errorMessage,
            statusMessage = action.statusMessage,
            linkedSession = action.linkedSession,
            latestExercisePlan = historyItems
                .sortedByDescending { it.receivedAt }
                .firstNotNullOfOrNull { parseRecommendedExercisePlan(it.rawJson) },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = RemoteConnectUiState(),
    )

    fun onManualQrChanged(value: String) {
        actionState.update {
            it.copy(
                manualQrValue = value,
                errorMessage = null,
            )
        }
    }

    fun connectManual() {
        connectFromQr(actionState.value.manualQrValue)
    }

    fun connectFromQr(rawValue: String) {
        val profile = uiState.value.selectedProfile
        if (profile == null) {
            actionState.update {
                it.copy(
                    errorMessage = "Select or create a profile first.",
                    isConnecting = false,
                )
            }
            return
        }

        val session = SteplyWebSessionLink.parse(rawValue)
        if (session == null) {
            actionState.update {
                it.copy(
                    errorMessage = "This is not a Steply Web QR code. Scan the QR code from the PC dashboard again.",
                    isConnecting = false,
                )
            }
            return
        }

        actionState.update {
            it.copy(
                isConnecting = true,
                errorMessage = null,
                statusMessage = "Linking this profile to the PC session.",
            )
        }

        webClient.connectProfile(
            session = session,
            profile = profile,
            callback = object : SteplyWebClient.ResultCallback {
                override fun onSuccess(body: String, connectedSession: SteplyWebSessionPayload) {
                    viewModelScope.launch {
                        appContainer.settingsRepository.setRemoteCameraHost(connectedSession.serverUrl)
                        actionState.update {
                            it.copy(
                                isConnecting = false,
                                statusMessage = "Linked through ${connectedSession.serverUrl}. You can now stream the phone camera to the PC.",
                                linkedSession = connectedSession,
                            )
                        }
                    }
                }

                override fun onFailure(message: String) {
                    actionState.update {
                        it.copy(
                            isConnecting = false,
                            errorMessage = "PC connection failed. Tried:\n$message",
                            statusMessage = "Check that both devices are on the same network and the PC server is running.",
                        )
                    }
                }
            },
        )
    }

    fun onLinkedSessionNavigationHandled() {
        actionState.update { it.copy(linkedSession = null) }
    }

    fun startManualCamera(serverUrl: String) {
        val normalized = serverUrl.trim().trimEnd('/')
        if (normalized.isBlank()) {
            actionState.update { it.copy(errorMessage = "Enter the PC web address. Example: http:///YOUR_PC_IP:3000") }
            return
        }
        val withScheme = if (normalized.startsWith("http://") || normalized.startsWith("https://")) {
            normalized
        } else {
            "http://$normalized"
        }
        actionState.update {
            it.copy(linkedSession = SteplyWebSessionPayload(ManualSessionId, withScheme))
        }
    }

    companion object {
        fun factory(appContainer: AppContainer) = viewModelFactory {
            RemoteConnectViewModel(appContainer)
        }
    }
}
