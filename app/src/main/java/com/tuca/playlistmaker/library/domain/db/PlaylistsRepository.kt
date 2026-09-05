package com.tuca.playlistmaker.library.domain.db

import android.net.Uri
import com.tuca.playlistmaker.library.domain.models.Playlist
import com.tuca.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistById(id: Int): Flow<Playlist>
    fun getTracksForPlaylist(trackIds: List<Long>): Flow<List<Track>>
    suspend fun createPlaylist(name: String, description: String?, coverUri: String?)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
    suspend fun removeTrackFromPlaylist(trackId: Long, playlist: Playlist)
    suspend fun saveImageToPrivateStorage(uri: Uri): String
}
