package com.tuca.playlistmaker.player.data.repository

import android.media.MediaPlayer
import com.tuca.playlistmaker.player.domain.api.AudioPlayerRepository

class AudioPlayerRepositoryImpl : AudioPlayerRepository {

    private val mediaPlayer = MediaPlayer()

    override fun prepare(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer.apply {
            setOnPreparedListener { onPrepared() }
            setOnCompletionListener { onCompletion() }
            setDataSource(url)
            prepareAsync()
        }
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }
}
