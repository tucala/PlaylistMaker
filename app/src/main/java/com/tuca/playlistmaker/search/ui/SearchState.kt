package com.tuca.playlistmaker.search.ui

import com.tuca.playlistmaker.player.domain.models.Track

data class SearchState(
    val query: String = "",
    val history: List<Track> = emptyList(),
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val isHistoryVisible: Boolean = false,
    val isTracksVisible: Boolean = false,
    val isEmptyStateVisible: Boolean = false,
    val isErrorStateVisible: Boolean = false
)
