package com.tuca.playlistmaker.domain.impl

import com.tuca.playlistmaker.domain.api.TrackInteractor
import com.tuca.playlistmaker.domain.api.TrackRepository
import java.util.concurrent.Executors

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TrackInteractor.TracksConsumer) {
        executor.execute {
            repository.searchTracks(expression) { foundTracks, code ->
                if (foundTracks != null) {
                    consumer.consume(foundTracks, null)
                } else {
                    consumer.consume(null, "Error")
                }
            }
        }
    }
}