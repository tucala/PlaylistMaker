package com.tuca.playlistmaker.library.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuca.playlistmaker.library.domain.db.PlaylistsInteractor
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _playlistCreated = MutableLiveData<String>()
    val playlistCreated: LiveData<String> get() = _playlistCreated

    fun createPlaylist(name: String, description: String?, coverUri: Uri?) {
        viewModelScope.launch {
            val savedCoverPath = coverUri?.let {
                playlistsInteractor.saveImageToPrivateStorage(it)
            }
            playlistsInteractor.createPlaylist(name, description, savedCoverPath)
            _playlistCreated.value = name
        }
    }
}
