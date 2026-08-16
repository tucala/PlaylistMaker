package com.tuca.playlistmaker.library.domain.db

import com.tuca.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavoriteTracks(): Flow<List<Track>>
    fun getFavoriteTrackIds(): Flow<List<Long>>
    suspend fun addFavoriteTrack(track: Track)
    suspend fun removeFavoriteTrack(track: Track)
}
