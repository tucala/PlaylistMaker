package com.tuca.playlistmaker.search.domain.impl

import com.tuca.playlistmaker.player.domain.models.Track
import com.tuca.playlistmaker.search.domain.api.TrackInteractor
import com.tuca.playlistmaker.search.domain.api.TrackRepository
import com.tuca.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> {
        return repository.searchTracks(expression)
    }
}

