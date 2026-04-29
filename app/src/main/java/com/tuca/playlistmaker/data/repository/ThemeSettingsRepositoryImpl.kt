package com.tuca.playlistmaker.data.repository

import android.content.SharedPreferences
import com.tuca.playlistmaker.domain.api.ThemeSettingsRepository

class ThemeSettingsRepositoryImpl(
    private val prefs: SharedPreferences
) : ThemeSettingsRepository {

    companion object {
        private const val KEY_DARK_THEME = "dark_theme"
    }

    override fun isDarkThemeEnabled(): Boolean = prefs.getBoolean(KEY_DARK_THEME, false)

    override fun setDarkThemeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_THEME, enabled).apply()
    }
}
