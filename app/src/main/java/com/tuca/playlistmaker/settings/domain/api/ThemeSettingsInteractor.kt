package com.tuca.playlistmaker.settings.domain.api

interface ThemeSettingsInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(isEnabled: Boolean)
}

class ThemeSettingsInteractorImpl(
    private val repository: com.tuca.playlistmaker.settings.domain.api.ThemeSettingsRepository
) : ThemeSettingsInteractor {

    override fun isDarkThemeEnabled(): Boolean {
        return repository.isDarkThemeEnabled()
    }

    override fun setDarkThemeEnabled(isEnabled: Boolean) {
        repository.setDarkThemeEnabled(isEnabled)
    }
}
