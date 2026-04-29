package com.tuca.playlistmaker.domain.api

import com.tuca.playlistmaker.domain.models.Track

interface HistoryInteractor {
    fun addTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}