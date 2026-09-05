package com.tuca.playlistmaker.library.domain.db

import android.net.Uri
import com.tuca.playlistmaker.library.domain.models.Playlist
import com.tuca.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val repository: PlaylistsRepository
) : PlaylistsInteractor {

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override fun getPlaylistById(id: Int): Flow<Playlist> {
        return repository.getPlaylistById(id)
    }

    override fun getTracksForPlaylist(trackIds: List<Long>): Flow<List<Track>> {
        return repository.getTracksForPlaylist(trackIds)
    }

    override suspend fun createPlaylist(name: String, description: String?, coverUri: String?) {
        repository.createPlaylist(name, description, coverUri)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        repository.addTrackToPlaylist(track, playlist)
    }

    override suspend fun removeTrackFromPlaylist(trackId: Long, playlist: Playlist) {
        repository.removeTrackFromPlaylist(trackId, playlist)
    }

    override suspend fun saveImageToPrivateStorage(uri: Uri): String {
        return repository.saveImageToPrivateStorage(uri)
    }
}
