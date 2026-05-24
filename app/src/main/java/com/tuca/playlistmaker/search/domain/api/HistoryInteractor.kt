package com.tuca.playlistmaker.search.domain.api

import com.tuca.playlistmaker.player.domain.models.Track

interface HistoryInteractor {
    fun addTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}

