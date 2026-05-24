package com.tuca.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.tuca.playlistmaker.creator.Creator

class PlaylistMakerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        applySavedTheme()
    }

    private fun applySavedTheme() {
        val settingsInteractor = Creator.provideThemeSettingsInteractor(applicationContext)
        AppCompatDelegate.setDefaultNightMode(
            if (settingsInteractor.isDarkThemeEnabled()) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
