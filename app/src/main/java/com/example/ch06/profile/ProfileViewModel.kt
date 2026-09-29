package com.example.ch06.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel(repository: ProfileRepository) : ViewModel() {
    private val initialProfile = repository.getProfile()
    private val _uiState = MutableStateFlow(
        ProfileUiState(initialProfile.username, initialProfile.notificationsEnabled)
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun onToggleNotification(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }
}
