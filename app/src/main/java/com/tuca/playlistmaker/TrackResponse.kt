package com.tuca.playlistmaker

data class TrackResponse(
    val resultCount: Int,
    val results: List<SearchActivity.Track>
)