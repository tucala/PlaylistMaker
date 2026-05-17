package com.tuca.playlistmaker.settings.data

import android.content.SharedPreferences

interface ThemeSettingsRepository {
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(isEnabled: Boolean)
}

class ThemeSettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : ThemeSettingsRepository {

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