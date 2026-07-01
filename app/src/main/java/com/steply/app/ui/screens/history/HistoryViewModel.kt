package com.steply.app.ui.screens.history

import androidx.lifecycle.ViewModel
import com.steply.app.AppContainer
import com.steply.app.domain.model.MovementHistory
import com.steply.app.ui.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope

data class HistoryUiState(
    val selectedUserId: String? = null,
    val items: List<MovementHistory> = emptyList(),
)

class HistoryViewModel(
    appContainer: AppContainer,
) : ViewModel() {
    val uiState: StateFlow<HistoryUiState> = combine(
        appContainer.settingsRepository.selectedUserId,
        appContainer.movementHistoryRepository.observeAll(),
    ) { selectedUserId, items ->
        HistoryUiState(
            selectedUserId = selectedUserId,
            items = selectedUserId?.let { id -> items.filter { it.profileId == id } } ?: items,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = HistoryUiState(),
    )

    companion object {
        fun factory(appContainer: AppContainer) = viewModelFactory {
            HistoryViewModel(appContainer)
        }
    }
}
