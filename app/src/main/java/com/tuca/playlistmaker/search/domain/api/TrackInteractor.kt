package com.tuca.playlistmaker.search.domain.api

import com.tuca.playlistmaker.player.domain.models.Track

interface TrackInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}

