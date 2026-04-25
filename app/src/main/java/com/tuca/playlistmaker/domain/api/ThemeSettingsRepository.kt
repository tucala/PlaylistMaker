package com.tuca.playlistmaker.domain.api

interface ThemeSettingsRepository {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}
