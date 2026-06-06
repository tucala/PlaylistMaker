package com.tuca.playlistmaker.search.domain.api

import com.tuca.playlistmaker.player.domain.models.Track
import java.util.concurrent.Executor

interface SearchInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)
    fun addTrack(track: Track)
    fun getHistory(): List<Track>
    fun clearHistory()

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}

class SearchInteractorImpl(
    private val repository: SearchRepository,
    private val executor: Executor
) : SearchInteractor {

    override fun searchTracks(expression: String, consumer: SearchInteractor.TracksConsumer) {
        executor.execute {
            repository.searchTracks(expression) { foundTracks, errorMessage ->
                consumer.consume(foundTracks, errorMessage)
            }
        }
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
