package com.tuca.playlistmaker.library.ui

import com.tuca.playlistmaker.player.domain.models.Track

sealed interface FavoritesState {
    object Empty : FavoritesState
    data class Content(val tracks: List<Track>) : FavoritesState
}
