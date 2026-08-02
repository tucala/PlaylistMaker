package com.tuca.playlistmaker.library.data.db

import com.tuca.playlistmaker.library.data.db.converter.TrackDbConverter
import com.tuca.playlistmaker.library.domain.db.FavoritesRepository
import com.tuca.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
) : FavoritesRepository {

    override fun getFavoriteTracks(): Flow<List<Track>> = flow {
        val entities = appDatabase.trackDao().getFavoriteTracks()
        emit(entities.map { trackDbConverter.map(it) })
    }.flowOn(Dispatchers.IO)

    override fun getFavoriteTrackIds(): Flow<List<Int>> = flow {
        val ids = appDatabase.trackDao().getFavoriteTrackIds()
        emit(ids ?: emptyList())
    }.flowOn(Dispatchers.IO)

    override suspend fun addFavoriteTrack(track: Track) = withContext(Dispatchers.IO) {
        val entity = trackDbConverter.map(track)
        appDatabase.trackDao().insertTrack(entity)
    }

    override suspend fun removeFavoriteTrack(track: Track) = withContext(Dispatchers.IO) {
        val entity = trackDbConverter.map(track)
        appDatabase.trackDao().deleteTrack(entity)
    }
}
