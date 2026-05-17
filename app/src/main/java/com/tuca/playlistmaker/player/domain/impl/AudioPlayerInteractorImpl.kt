package com.tuca.playlistmaker.player.domain.impl

import com.tuca.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.tuca.playlistmaker.player.domain.api.AudioPlayerRepository

class AudioPlayerInteractorImpl(
    private val repository: AudioPlayerRepository
) : AudioPlayerInteractor {

    override fun preparePlayer(previewUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        repository.prepare(previewUrl, onPrepared, onCompletion)
    }

    override fun startPlayer() {
        repository.start()
    }

    override fun pausePlayer() {
        repository.pause()
    }

    override fun releasePlayer() {
        repository.release()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }
}

