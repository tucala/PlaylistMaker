package com.tuca.playlistmaker.domain.impl

import com.tuca.playlistmaker.domain.api.ThemeSettingsInteractor
import com.tuca.playlistmaker.domain.api.ThemeSettingsRepository

class ThemeSettingsInteractorImpl(
    private val repository: ThemeSettingsRepository
) : ThemeSettingsInteractor {
    override fun isDarkThemeEnabled(): Boolean = repository.isDarkThemeEnabled()

    override fun setDarkThemeEnabled(enabled: Boolean) {
        repository.setDarkThemeEnabled(enabled)
    }
}
