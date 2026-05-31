package com.tuca.playlistmaker.search.domain.api

import com.tuca.playlistmaker.player.domain.models.Track

interface SearchRepository {
    fun searchTracks(expression: String, callback: (List<Track>?, String?) -> Unit)
    fun addTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}
