package com.tuca.playlistmaker.domain.impl

import com.tuca.playlistmaker.domain.api.AudioPlayerRepository
import com.tuca.playlistmaker.domain.api.AudioPlayerInteractor

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
