package com.tuca.playlistmaker.player.ui

import com.tuca.playlistmaker.player.domain.models.Track

data class PlayerState(
    val track: Track,
    val currentTimeText: String = "00:00",
    val isPlaying: Boolean = false,
    val isPlayButtonEnabled: Boolean = false
)

