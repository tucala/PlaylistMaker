package com.tuca.playlistmaker.data.dto

import com.tuca.playlistmaker.domain.models.Track

data class TrackResponse(
    val resultCount: Int,
    val results: List<Track>
) : Response()