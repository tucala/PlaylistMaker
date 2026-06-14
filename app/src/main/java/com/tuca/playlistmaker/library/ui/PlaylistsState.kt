package com.tuca.playlistmaker.library.ui

sealed interface PlaylistsState {
    object Empty : PlaylistsState
    data class Content(val playlists: List<Any>) : PlaylistsState
}
