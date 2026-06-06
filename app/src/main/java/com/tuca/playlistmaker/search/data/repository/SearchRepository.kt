package com.tuca.playlistmaker.search.data.repository

import com.tuca.playlistmaker.player.domain.models.Track
import com.tuca.playlistmaker.search.domain.api.HistoryRepository
import com.tuca.playlistmaker.search.domain.api.SearchRepository
import com.tuca.playlistmaker.search.domain.api.TrackRepository

class SearchRepositoryImpl(
    private val trackRepository: TrackRepository,
    private val historyRepository: HistoryRepository
) : SearchRepository {

    override fun searchTracks(expression: String, callback: (List<Track>?, String?) -> Unit) {
        trackRepository.searchTracks(expression) { foundTracks, _ ->
            if (foundTracks != null) {
                callback(foundTracks, null)
            } else {
                callback(null, "Error")
            }
        }
    }

    override fun addTrack(track: Track) {
        historyRepository.addTrack(track)
    }

    override fun getHistory(): List<Track> {
        return historyRepository.getHistory()
    }

    override fun clearHistory() {
        historyRepository.clearHistory()
    }
}
