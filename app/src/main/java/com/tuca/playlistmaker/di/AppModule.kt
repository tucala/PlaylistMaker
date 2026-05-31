package com.tuca.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.tuca.playlistmaker.R
import com.tuca.playlistmaker.main.ui.MainViewModel
import com.tuca.playlistmaker.player.data.repository.AudioPlayerRepositoryImpl
import com.tuca.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.tuca.playlistmaker.player.domain.api.AudioPlayerRepository
import com.tuca.playlistmaker.player.domain.impl.AudioPlayerInteractorImpl
import com.tuca.playlistmaker.player.domain.models.Track
import com.tuca.playlistmaker.player.ui.PlayerViewModel
import com.tuca.playlistmaker.search.data.network.ITunesApi
import com.tuca.playlistmaker.search.data.network.NetworkClient
import com.tuca.playlistmaker.search.data.network.RetrofitNetworkClient
import com.tuca.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.tuca.playlistmaker.search.data.repository.SearchRepository
import com.tuca.playlistmaker.search.data.repository.SearchRepositoryImpl
import com.tuca.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.tuca.playlistmaker.search.domain.api.HistoryRepository
import com.tuca.playlistmaker.search.domain.api.SearchInteractor
import com.tuca.playlistmaker.search.domain.api.SearchInteractorImpl
import com.tuca.playlistmaker.search.domain.api.TrackRepository
import com.tuca.playlistmaker.search.ui.SearchViewModel
import com.tuca.playlistmaker.settings.data.ThemeSettingsRepository
import com.tuca.playlistmaker.settings.data.ThemeSettingsRepositoryImpl
import com.tuca.playlistmaker.settings.domain.api.ThemeSettingsInteractor
import com.tuca.playlistmaker.settings.domain.api.ThemeSettingsInteractorImpl
import com.tuca.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.core.qualifier.named
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private const val LOCAL_STORAGE_PREFS = "local_storage"
private const val SETTINGS_PREFS = "settings"
private val localStoragePrefsQualifier = named(LOCAL_STORAGE_PREFS)
private val settingsPrefsQualifier = named(SETTINGS_PREFS)

val appModule = module {
    single(localStoragePrefsQualifier) {
        androidContext().getSharedPreferences(LOCAL_STORAGE_PREFS, Context.MODE_PRIVATE)
    }
    single(settingsPrefsQualifier) {
        androidContext().getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
    }

    single<ThemeSettingsRepository> { ThemeSettingsRepositoryImpl(get(localStoragePrefsQualifier)) }
    single<ThemeSettingsInteractor> { ThemeSettingsInteractorImpl(get()) }

    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }
    single<NetworkClient> { RetrofitNetworkClient(get()) }
    single<TrackRepository> { TrackRepositoryImpl(get()) }
    single<HistoryRepository> { HistoryRepositoryImpl(get(settingsPrefsQualifier)) }
    single<SearchRepository> { SearchRepositoryImpl(get(), get()) }
    single<Executor> { Executors.newCachedThreadPool() }
    single<SearchInteractor> { SearchInteractorImpl(get(), get()) }

    factory { MediaPlayer() }
    factory<AudioPlayerRepository> { AudioPlayerRepositoryImpl(get()) }
    factory<AudioPlayerInteractor> { AudioPlayerInteractorImpl(get()) }

    viewModel { MainViewModel() }
    viewModel { SearchViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { (track: Track) ->
        PlayerViewModel(track, get(), androidContext().getString(R.string.zeroTime))
    }
}
