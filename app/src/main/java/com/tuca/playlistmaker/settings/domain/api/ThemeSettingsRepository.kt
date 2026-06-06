package com.tuca.playlistmaker.settings.domain.api

interface ThemeSettingsRepository {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(isEnabled: Boolean)
}
