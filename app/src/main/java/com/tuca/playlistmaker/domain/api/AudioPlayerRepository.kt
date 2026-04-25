package com.tuca.playlistmaker.domain.api

interface AudioPlayerRepository {
    fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun start()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
}