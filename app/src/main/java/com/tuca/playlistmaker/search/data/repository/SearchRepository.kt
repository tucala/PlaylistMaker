package com.tuca.playlistmaker.search.data.repository

import com.tuca.playlistmaker.player.domain.models.Track
import com.tuca.playlistmaker.search.domain.api.HistoryRepository
import com.tuca.playlistmaker.search.domain.api.SearchRepository
import com.tuca.playlistmaker.search.domain.api.TrackRepository
import com.tuca.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

class SearchRepositoryImpl(
    private val trackRepository: TrackRepository,
    private val historyRepository: HistoryRepository
) : SearchRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> {
        return trackRepository.searchTracks(expression)
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
