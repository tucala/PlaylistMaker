package com.tuca.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuca.playlistmaker.library.domain.db.FavoritesInteractor
import com.tuca.playlistmaker.library.domain.db.PlaylistsInteractor
import com.tuca.playlistmaker.library.domain.models.Playlist
import com.tuca.playlistmaker.player.domain.models.Track
import com.tuca.playlistmaker.player.domain.api.AudioPlayerInteractor
import com.tuca.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
    private val zeroTimeText: String
) : ViewModel() {

    private val _state = MutableLiveData(PlayerState(track = track, isFavorite = track.isFavorite))
    val state: LiveData<PlayerState> get() = _state

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> get() = _playlists

    private val _playlistAdditionEvent = SingleLiveEvent<Pair<Boolean, String>>()
    val playlistAdditionEvent: LiveData<Pair<Boolean, String>> get() = _playlistAdditionEvent

    private var timerJob: Job? = null

    init {
        preparePlayer()
        observeFavoriteStatus()
    }

    private fun observeFavoriteStatus() {
        viewModelScope.launch {
            favoritesInteractor.getFavoriteTrackIds().collect { favoriteIds ->
                val isFav = favoriteIds.contains(track.trackId)
                track.isFavorite = isFav
                updateState { copy(isFavorite = isFav) }
            }
        }
    }

    fun onFavoriteClicked() {
        val currentState = _state.value ?: return
        viewModelScope.launch {
            if (currentState.isFavorite) {
                favoritesInteractor.removeFavoriteTrack(track)
                track.isFavorite = false
                updateState { copy(isFavorite = false) }
            } else {
                favoritesInteractor.addFavoriteTrack(track)
                track.isFavorite = true
                updateState { copy(isFavorite = true) }
            }
        }
    }

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlistsList ->
                _playlists.value = playlistsList
            }
        }
    }

    fun onPlaylistClicked(playlist: Playlist) {
        if (playlist.trackIds.contains(track.trackId)) {
            _playlistAdditionEvent.value = Pair(false, playlist.name)
        } else {
            viewModelScope.launch {
                playlistsInteractor.addTrackToPlaylist(track, playlist)
                _playlistAdditionEvent.value = Pair(true, playlist.name)
                loadPlaylists()
            }
        }
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
                updateState { copy(isPlayButtonEnabled = true, currentTimeText = zeroTimeText) }
            },
            onCompletion = {
                timerJob?.cancel()
                updateState {
                    copy(
                        isPlaying = false,
                        currentTimeText = zeroTimeText
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
        private const val TIMER_DELAY = 300L
    }
}
