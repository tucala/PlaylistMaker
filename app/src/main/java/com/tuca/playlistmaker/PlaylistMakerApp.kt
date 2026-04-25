package com.tuca.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class PlaylistMakerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Creator.init(this)
        applySavedTheme()
    }

    private fun applySavedTheme() {
        val settingsInteractor = Creator.provideThemeSettingsInteractor()
        AppCompatDelegate.setDefaultNightMode(
            if (settingsInteractor.isDarkThemeEnabled()) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
