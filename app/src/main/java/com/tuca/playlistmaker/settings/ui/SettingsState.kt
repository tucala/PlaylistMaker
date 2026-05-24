package com.tuca.playlistmaker.settings.ui

sealed interface SettingsState {
    data class Content(val isDarkModeEnabled: Boolean) : SettingsState
    object ShareApp : SettingsState
    object ContactSupport : SettingsState
    object OpenTerms : SettingsState
}