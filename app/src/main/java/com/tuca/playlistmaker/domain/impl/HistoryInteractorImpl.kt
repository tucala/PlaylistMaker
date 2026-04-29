package com.tuca.playlistmaker.domain.impl

import com.tuca.playlistmaker.domain.api.HistoryInteractor
import com.tuca.playlistmaker.domain.api.HistoryRepository
import com.tuca.playlistmaker.domain.models.Track

class HistoryInteractorImpl(private val repository: HistoryRepository) : HistoryInteractor {
    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}