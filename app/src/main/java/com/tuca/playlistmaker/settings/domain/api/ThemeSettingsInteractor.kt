package com.tuca.playlistmaker.settings.domain.api

import com.tuca.playlistmaker.settings.data.ThemeSettingsRepository

interface ThemeSettingsInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(isEnabled: Boolean)
}

class ThemeSettingsInteractorImpl(
    private val repository: ThemeSettingsRepository
) : ThemeSettingsInteractor {

    override fun isDarkThemeEnabled(): Boolean {
        return repository.isDarkThemeEnabled()
    }

    override fun setDarkThemeEnabled(isEnabled: Boolean) {
        repository.setDarkThemeEnabled(isEnabled)
    }
}