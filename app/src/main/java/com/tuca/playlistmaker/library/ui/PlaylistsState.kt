package com.tuca.playlistmaker.library.ui

import com.tuca.playlistmaker.library.domain.models.Playlist

sealed interface PlaylistsState {
    object Empty : PlaylistsState
    data class Content(val playlists: List<Playlist>) : PlaylistsState
}

