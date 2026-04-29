package com.tuca.playlistmaker

import android.content.Context
import com.tuca.playlistmaker.data.network.ITunesApi
import com.tuca.playlistmaker.data.network.NetworkClient
import com.tuca.playlistmaker.data.network.RetrofitNetworkClient
import com.tuca.playlistmaker.data.repository.AudioPlayerRepositoryImpl
import com.tuca.playlistmaker.data.repository.HistoryRepositoryImpl
import com.tuca.playlistmaker.data.repository.ThemeSettingsRepositoryImpl
import com.tuca.playlistmaker.data.repository.TrackRepositoryImpl
import com.tuca.playlistmaker.domain.api.AudioPlayerInteractor
import com.tuca.playlistmaker.domain.api.AudioPlayerRepository
import com.tuca.playlistmaker.domain.api.HistoryInteractor
import com.tuca.playlistmaker.domain.api.HistoryRepository
import com.tuca.playlistmaker.domain.api.ThemeSettingsInteractor
import com.tuca.playlistmaker.domain.api.TrackInteractor
import com.tuca.playlistmaker.domain.api.TrackRepository
import com.tuca.playlistmaker.domain.impl.AudioPlayerInteractorImpl
import com.tuca.playlistmaker.domain.impl.HistoryInteractorImpl
import com.tuca.playlistmaker.domain.impl.ThemeSettingsInteractorImpl
import com.tuca.playlistmaker.domain.impl.TrackInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private lateinit var applicationContext: android.content.Context

    fun init(context: android.content.Context) {
        applicationContext = context
    }

    private fun getHistoryRepository(): HistoryRepository {
        return HistoryRepositoryImpl(
            applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
        )
    }

    fun provideHistoryInteractor(): HistoryInteractor {
        return HistoryInteractorImpl(getHistoryRepository())
    }

    fun provideThemeSettingsInteractor(): ThemeSettingsInteractor {
        return ThemeSettingsInteractorImpl(
            ThemeSettingsRepositoryImpl(
                applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
            )
        )
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
    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    private fun getAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl()
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(getAudioPlayerRepository())
    }
}
