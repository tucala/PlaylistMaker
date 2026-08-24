package com.tuca.playlistmaker.library.ui

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tuca.playlistmaker.library.domain.db.PlaylistsInteractor
import com.tuca.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    playlistsInteractor: PlaylistsInteractor
) : NewPlaylistViewModel(playlistsInteractor) {

    private val _editingPlaylist = MutableLiveData<Playlist>()
    val editingPlaylist = _editingPlaylist

    private var currentPlaylist: Playlist? = null

    fun initPlaylist(playlist: Playlist) {
        currentPlaylist = playlist
        _editingPlaylist.value = playlist
    }

    fun updatePlaylist(name: String, description: String?, coverUri: Uri?) {
        val playlist = currentPlaylist ?: return
        viewModelScope.launch {
            val savedCoverPath = coverUri?.let {
                playlistsInteractor.saveImageToPrivateStorage(it)
            } ?: playlist.coverPath
            val updated = playlist.copy(
                name = name,
                description = description?.ifBlank { null },
                coverPath = savedCoverPath
            )
            playlistsInteractor.updatePlaylist(updated)
            _playlistCreated.value = name
        }
    }
}