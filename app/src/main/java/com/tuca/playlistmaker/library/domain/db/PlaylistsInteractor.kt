package com.tuca.playlistmaker.library.domain.db

import android.net.Uri
import com.tuca.playlistmaker.library.domain.models.Playlist
import com.tuca.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun createPlaylist(name: String, description: String?, coverUri: String?)
    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)
    suspend fun saveImageToPrivateStorage(uri: Uri): String
}
