package com.tuca.playlistmaker.library.domain.models

import java.io.Serializable

data class Playlist(
    val id: Int = 0,
    val name: String,
    val description: String? = null,
    val coverPath: String? = null,
    val trackIds: List<Long> = emptyList(),
    val tracksCount: Int = 0
) : Serializable
