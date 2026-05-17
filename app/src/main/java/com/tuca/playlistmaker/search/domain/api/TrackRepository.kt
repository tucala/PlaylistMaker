package com.tuca.playlistmaker.search.domain.api

import com.tuca.playlistmaker.player.domain.models.Track

interface TrackRepository {
    fun searchTracks(query: String, callback: (List<Track>?, Int) -> Unit)
}

