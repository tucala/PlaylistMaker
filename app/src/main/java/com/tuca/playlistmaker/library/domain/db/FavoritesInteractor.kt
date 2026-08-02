package com.tuca.playlistmaker.library.domain.db

import com.tuca.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    fun getFavoriteTracks(): Flow<List<Track>>
    fun getFavoriteTrackIds(): Flow<List<Int>>
    suspend fun addFavoriteTrack(track: Track)
    suspend fun removeFavoriteTrack(track: Track)
}
