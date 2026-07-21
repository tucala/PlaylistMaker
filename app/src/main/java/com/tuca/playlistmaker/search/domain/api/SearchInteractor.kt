package com.tuca.playlistmaker.search.domain.api

import com.tuca.playlistmaker.player.domain.models.Track
import com.tuca.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
    fun addTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()
}

class SearchInteractorImpl(
    private val repository: SearchRepository
) : SearchInteractor {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> {
        return repository.searchTracks(expression)
    }

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
