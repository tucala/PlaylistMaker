package com.tuca.playlistmaker.domain.api

import com.tuca.playlistmaker.domain.models.Track

interface TrackRepository {
    fun searchTracks(query: String, callback: (List<Track>?, Int) -> Unit)
}