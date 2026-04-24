package com.tuca.playlistmaker.data.repository

import com.tuca.playlistmaker.domain.api.HistoryRepository
import com.tuca.playlistmaker.domain.models.Track
import com.tuca.playlistmaker.ui.track.SearchHistory

class HistoryRepositoryImpl(private val historyStorage: SearchHistory) : HistoryRepository {
    override fun addTrack(track: Track) = historyStorage.addTrack(track)
    override fun getHistory(): List<Track> = historyStorage.getHistory()
    override fun clearHistory() = historyStorage.clear()
}