package com.tuca.playlistmaker.data.repository

import com.tuca.playlistmaker.data.dto.TrackResponse
import com.tuca.playlistmaker.data.dto.TrackSearchRequest
import com.tuca.playlistmaker.data.network.NetworkClient
import com.tuca.playlistmaker.domain.api.TrackRepository
import com.tuca.playlistmaker.domain.models.Track

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    override fun searchTracks(query: String, callback: (List<Track>?, Int) -> Unit) {
        val response = networkClient.doRequest(TrackSearchRequest(query))
        if (response.resultCode == 200) {
            val tracks = (response as TrackResponse).results.map { dto ->
                Track(
                    trackName = dto.trackName ?: "",
                    artistName = dto.artistName ?: "",
                    previewUrl = dto.previewUrl ?: "",
                    trackTimeMillis = dto.trackTimeMillis,
                    artworkUrl100 = dto.artworkUrl100 ?: "",
                    collectionName = dto.collectionName ?: "",
                    releaseDate = dto.releaseDate ?: "",
                    primaryGenreName = dto.primaryGenreName ?: "",
                    country = dto.country ?: ""
                )
            }
            callback(tracks, response.resultCode)
        } else {
            callback(null, response.resultCode)
        }
    }
}