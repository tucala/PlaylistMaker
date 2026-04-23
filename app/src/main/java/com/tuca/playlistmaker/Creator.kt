package com.tuca.playlistmaker

import com.tuca.playlistmaker.data.TrackRepositoryImpl
import com.tuca.playlistmaker.data.network.ITunesApi
import com.tuca.playlistmaker.data.network.NetworkClient
import com.tuca.playlistmaker.data.network.RetrofitNetworkClient
import com.tuca.playlistmaker.domain.api.TrackInteractor
import com.tuca.playlistmaker.domain.api.TrackRepository
import com.tuca.playlistmaker.domain.impl.TrackInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    // 1. Создаем API через Retrofit
    private fun getApi(): ITunesApi {
        return Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    // 2. Создаем сетевой клиент (теперь он требует API)
    private fun getNetworkClient(): NetworkClient {
        return RetrofitNetworkClient(getApi())
    }

    // 3. Создаем репозиторий (теперь он требует NetworkClient, а не API!)
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(getNetworkClient())
    }

    // 4. Создаем интерактор для Activity
    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }
}