package com.tuca.playlistmaker.library.domain.db

import com.tuca.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository
) : FavoritesInteractor {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }

    override fun getFavoriteTrackIds(): Flow<List<Long>> {
        return repository.getFavoriteTrackIds()
    }

    override suspend fun addFavoriteTrack(track: Track) {
        repository.addFavoriteTrack(track)
    }

    override suspend fun removeFavoriteTrack(track: Track) {
        repository.removeFavoriteTrack(track)
    }
}
