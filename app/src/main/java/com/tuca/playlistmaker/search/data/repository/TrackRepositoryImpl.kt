package com.tuca.playlistmaker.search.data.repository

import com.tuca.playlistmaker.player.domain.models.Track
import com.tuca.playlistmaker.search.data.dto.TrackResponse
import com.tuca.playlistmaker.search.data.dto.TrackSearchRequest
import com.tuca.playlistmaker.search.data.network.NetworkClient
import com.tuca.playlistmaker.search.domain.api.TrackRepository
import com.tuca.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(private val networkClient: NetworkClient) : TrackRepository {

    override fun searchTracks(query: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(query))
        if (response.resultCode == 200) {
            val tracks = (response as TrackResponse).results.map { dto ->
                Track(
                    trackId = dto.trackId ?: 0,
                    trackName = dto.trackName,
                    artistName = dto.artistName,
                    previewUrl = dto.previewUrl,
                    trackTimeMillis = dto.trackTimeMillis,
                    artworkUrl100 = dto.artworkUrl100,
                    collectionName = dto.collectionName,
                    releaseDate = dto.releaseDate,
                    primaryGenreName = dto.primaryGenreName,
                    country = dto.country
                )
            }
            emit(Resource.Success(tracks))
        } else {
            emit(Resource.Error("Server error code: ${response.resultCode}"))
        }
    }
}
