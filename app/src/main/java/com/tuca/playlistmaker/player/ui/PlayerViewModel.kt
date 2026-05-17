package com.tuca.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.tuca.playlistmaker.player.domain.models.Track
import com.tuca.playlistmaker.player.domain.api.AudioPlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val audioPlayerInteractor: AudioPlayerInteractor
) : ViewModel() {

    private val _state = MutableLiveData(PlayerState(track = track))
    val state: LiveData<PlayerState> get() = _state

    private var timerJob: Job? = null

    init {
        preparePlayer()
    }

    fun onPlayClicked() {
        val state = _state.value ?: return
        if (!state.isPlayButtonEnabled) return

        if (state.isPlaying) {
            pausePlayer()
        } else {
            startPlayer()
        }
    }

    fun onPauseFromUi() {
        val state = _state.value ?: return
        if (state.isPlaying) {
            pausePlayer()
        }
    }

    fun onClearedFromUi() {
        timerJob?.cancel()
        audioPlayerInteractor.releasePlayer()
    }

    private fun preparePlayer() {
        val previewUrl = track.previewUrl
        if (previewUrl.isNullOrEmpty()) return

        audioPlayerInteractor.preparePlayer(
            previewUrl = previewUrl,
            onPrepared = {
                updateState { copy(isPlayButtonEnabled = true, currentTimeText = "00:00") }
            },
            onCompletion = {
                timerJob?.cancel()
                updateState {
                    copy(
                        isPlaying = false,
                        currentTimeText = "00:00"
                    )
                }
            }
        )
    }

    private fun startPlayer() {
        audioPlayerInteractor.startPlayer()
        updateState { copy(isPlaying = true) }
        startTimer()
    }

    private fun pausePlayer() {
        audioPlayerInteractor.pausePlayer()
        timerJob?.cancel()
        updateState { copy(isPlaying = false) }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                updateState {
                    copy(currentTimeText = formatTime(audioPlayerInteractor.getCurrentPosition()))
                }
                delay(TIMER_DELAY)
            }
        }
    }

    private fun formatTime(millis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
    }

    private inline fun updateState(transform: PlayerState.() -> PlayerState) {
        val currentState = _state.value ?: PlayerState(track)
        _state.value = currentState.transform()
    }

    companion object {
        private const val TIMER_DELAY = 500L
    }
}

class PlayerViewModelFactory(
    private val track: Track,
    private val audioPlayerInteractor: AudioPlayerInteractor
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return PlayerViewModel(track, audioPlayerInteractor) as T
    }
}

