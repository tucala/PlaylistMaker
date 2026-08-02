package com.tuca.playlistmaker.library.domain.db

import com.tuca.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
) : FavoritesInteractor {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoritesRepository.getFavoriteTracks()
    }

    override fun getFavoriteTrackIds(): Flow<List<Int>> {
        return favoritesRepository.getFavoriteTrackIds()
    }

    override suspend fun addFavoriteTrack(track: Track) {
        favoritesRepository.addFavoriteTrack(track)
    }

    override suspend fun removeFavoriteTrack(track: Track) {
        favoritesRepository.removeFavoriteTrack(track)
    }
}
