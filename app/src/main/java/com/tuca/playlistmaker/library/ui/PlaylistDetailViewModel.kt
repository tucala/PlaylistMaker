package com.tuca.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuca.playlistmaker.library.domain.db.PlaylistsInteractor
import com.tuca.playlistmaker.library.domain.models.Playlist
import com.tuca.playlistmaker.player.domain.models.Track
import com.tuca.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PlaylistDetailViewModel(
    private val playlistId: Int,
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> get() = _playlist

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> get() = _tracks

    private val _playlistDeleted = SingleLiveEvent<Unit>()
    val playlistDeleted: LiveData<Unit> get() = _playlistDeleted

    private var loadJob: Job? = null

    init {
        loadPlaylist()
    }

    fun loadPlaylist() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val currentPlaylist = playlistsInteractor.getPlaylistById(playlistId).firstOrNull()
                ?: return@launch
            _playlist.value = currentPlaylist
            _tracks.value = playlistsInteractor
                .getTracksForPlaylist(currentPlaylist.trackIds)
                .firstOrNull()
                .orEmpty()
        }
    }

    fun removeTrack(track: Track) {
        val currentPlaylist = _playlist.value ?: return
        viewModelScope.launch {
            playlistsInteractor.removeTrackFromPlaylist(track.trackId, currentPlaylist)
            loadPlaylist()
        }
    }

    fun deletePlaylist() {
        val currentPlaylist = _playlist.value ?: return
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(currentPlaylist)
            _playlistDeleted.value = Unit
        }
    }

    fun getTotalDurationText(): String {
        val trackList = _tracks.value ?: return "0 минут"
        val totalMillis = trackList.sumOf { it.trackTimeMillis }
        val minutes = totalMillis / 60_000
        return "$minutes минут"
    }

    fun buildShareText(): String? {
        val pl = _playlist.value ?: return null
        val trackList = _tracks.value ?: emptyList()
        if (trackList.isEmpty()) return null
        val sb = StringBuilder()
        sb.appendLine(pl.name)
        if (!pl.description.isNullOrBlank()) sb.appendLine(pl.description)
        sb.appendLine("${trackList.size} треков")
        trackList.forEachIndexed { index, track ->
            val dur = SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(track.trackTimeMillis))
            sb.appendLine("${index + 1}. ${track.artistName} - ${track.trackName} ($dur)")
        }
        return sb.toString().trimEnd()
    }
}
