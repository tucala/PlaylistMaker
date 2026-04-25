package com.tuca.playlistmaker.domain.api

interface ThemeSettingsInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}
