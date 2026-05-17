package com.tuca.playlistmaker.search.domain.impl

import com.tuca.playlistmaker.player.domain.models.Track
import com.tuca.playlistmaker.search.domain.api.HistoryInteractor
import com.tuca.playlistmaker.search.domain.api.HistoryRepository

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

