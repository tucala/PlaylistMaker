package com.tuca.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.tuca.playlistmaker.di.appModule
import com.tuca.playlistmaker.settings.domain.api.ThemeSettingsInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class PlaylistMakerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PlaylistMakerApp)
            modules(appModule)
        }
        applySavedTheme()
    }

    private fun applySavedTheme() {
        val settingsInteractor = GlobalContext.get().get<ThemeSettingsInteractor>()
        AppCompatDelegate.setDefaultNightMode(
            if (settingsInteractor.isDarkThemeEnabled()) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
