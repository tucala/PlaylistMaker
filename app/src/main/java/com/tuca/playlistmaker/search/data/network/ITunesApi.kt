package com.tuca.playlistmaker.search.data.network

import com.tuca.playlistmaker.search.data.dto.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {

    @GET("search?entity=song")
    fun search(
        @Query("term") text: String,
        @Query("entity") entity: String = "song"
    ): Call<TrackResponse>
}

