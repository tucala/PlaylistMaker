package com.tuca.playlistmaker.domain.impl

import android.media.MediaPlayer
import com.tuca.playlistmaker.domain.api.AudioPlayerInteractor

class AudioPlayerInteractorImpl : AudioPlayerInteractor {
    private val mediaPlayer = MediaPlayer()

    override fun preparePlayer(previewUrl: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener { onPrepared() }
        mediaPlayer.setOnCompletionListener { onCompletion() }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
}