package com.tuca.playlistmaker.creator

import android.content.Context
import com.tuca.playlistmaker.player.data.repository.AudioPlayerRepositoryImpl
import com.tuca.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.tuca.playlistmaker.player.domain.api.AudioPlayerRepository
import com.tuca.playlistmaker.player.domain.impl.AudioPlayerInteractorImpl
import com.tuca.playlistmaker.search.data.network.ITunesApi
import com.tuca.playlistmaker.search.data.network.NetworkClient
import com.tuca.playlistmaker.search.data.network.RetrofitNetworkClient
import com.tuca.playlistmaker.search.data.repository.HistoryRepositoryImpl
import com.tuca.playlistmaker.search.data.repository.SearchRepositoryImpl
import com.tuca.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.tuca.playlistmaker.search.domain.api.HistoryRepository
import com.tuca.playlistmaker.search.domain.api.SearchInteractor
import com.tuca.playlistmaker.search.domain.api.SearchInteractorImpl
import com.tuca.playlistmaker.search.domain.api.TrackRepository
import com.tuca.playlistmaker.settings.data.ThemeSettingsRepositoryImpl
import com.tuca.playlistmaker.settings.domain.api.ThemeSettingsInteractor
import com.tuca.playlistmaker.settings.domain.api.ThemeSettingsInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private fun getThemeRepository(context: Context): ThemeSettingsRepositoryImpl {
        val sharedPrefs = context.getSharedPreferences("local_storage", Context.MODE_PRIVATE)
        return ThemeSettingsRepositoryImpl(sharedPrefs)
    }

    fun provideThemeSettingsInteractor(context: Context): ThemeSettingsInteractor {
        return ThemeSettingsInteractorImpl(getThemeRepository(context))
    }

    private fun getApi(): ITunesApi {
        return Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    private fun getNetworkClient(): NetworkClient {
        return RetrofitNetworkClient(getApi())
    }

    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(getNetworkClient())
    }

    private fun getHistoryRepository(context: Context): HistoryRepository {
        return HistoryRepositoryImpl(
            context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        )
    }

    private fun getSearchRepository(context: Context): SearchRepositoryImpl {
        return SearchRepositoryImpl(
            getTrackRepository(),
            getHistoryRepository(context)
        )
    }

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(getSearchRepository(context))
    }

    private fun getAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl()
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(getAudioPlayerRepository())
    }
}
