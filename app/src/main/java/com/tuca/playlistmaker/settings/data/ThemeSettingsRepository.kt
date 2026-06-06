package com.tuca.playlistmaker.settings.data

import android.content.SharedPreferences

class ThemeSettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : com.tuca.playlistmaker.settings.domain.api.ThemeSettingsRepository {

    companion object {
        private const val DARK_THEME_KEY = "dark_theme_key"
    }

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    override fun setDarkThemeEnabled(isEnabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, isEnabled)
            .apply()
    }
}
