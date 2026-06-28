package com.tuca.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuca.playlistmaker.player.domain.models.Track
import com.tuca.playlistmaker.search.domain.api.SearchInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor
) : ViewModel() {

    private val _state = MutableLiveData(SearchState())
    val state: LiveData<SearchState> get() = _state

    private var searchJob: Job? = null
    private var currentQuery: String = ""

    init {
        loadHistory()
    }

    fun onQueryChanged(query: String) {
        if (query == currentQuery) return
        currentQuery = query
        searchJob?.cancel()

        if (query.isBlank()) {
            loadHistory()
            return
        }

        updateState {
            copy(
                query = query,
                isLoading = false,
                isHistoryVisible = false,
                isTracksVisible = false,
                isEmptyStateVisible = false,
                isErrorStateVisible = false
            )
        }

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            if (query != currentQuery) return@launch
            updateState { copy(isLoading = true) }
            searchTracks(query)
        }
    }

    fun onSearchAction() {
        searchJob?.cancel()

        if (currentQuery.isBlank()) {
            loadHistory()
            return
        }

        updateState {
            copy(
                query = currentQuery,
                isLoading = true,
                isHistoryVisible = false,
                isTracksVisible = false,
                isEmptyStateVisible = false,
                isErrorStateVisible = false
            )
        }
        searchTracks(currentQuery)
    }

    fun onRetryClicked() {
        searchJob?.cancel()

        if (currentQuery.isBlank()) {
            loadHistory()
            return
        }

        updateState {
            copy(
                query = currentQuery,
                isLoading = true,
                isHistoryVisible = false,
                isTracksVisible = false,
                isEmptyStateVisible = false,
                isErrorStateVisible = false
            )
        }
        searchTracks(currentQuery)
    }

    fun onTrackClicked(track: Track) {
        searchInteractor.addTrack(track)
        refreshHistory(preserveSearchResults = true)
    }

    fun onClearHistoryClicked() {
        searchInteractor.clearHistory()
        refreshHistory(preserveSearchResults = true)
    }

    private fun searchTracks(query: String) {
        searchInteractor.searchTracks(query, object : SearchInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                viewModelScope.launch {
                    if (query != currentQuery) return@launch

                    if (foundTracks != null) {
                        updateState {
                            copy(
                                query = query,
                                tracks = foundTracks,
                                isLoading = false,
                                isHistoryVisible = false,
                                isTracksVisible = foundTracks.isNotEmpty(),
                                isEmptyStateVisible = foundTracks.isEmpty(),
                                isErrorStateVisible = false
                            )
                        }
                    } else {
                        updateState {
                            copy(
                                query = query,
                                tracks = emptyList(),
                                isLoading = false,
                                isHistoryVisible = false,
                                isTracksVisible = false,
                                isEmptyStateVisible = false,
                                isErrorStateVisible = true
                            )
                        }
                    }
                }
            }
        })
    }

    private fun loadHistory() {
        refreshHistory(preserveSearchResults = false)
    }

    private fun refreshHistory(preserveSearchResults: Boolean) {
        val history = searchInteractor.getHistory()
        val showHistory = currentQuery.isBlank() && history.isNotEmpty()
        val currentState = _state.value ?: SearchState()

        updateState {
            copy(
                query = currentQuery,
                history = history,
                tracks = if (preserveSearchResults) currentState.tracks else emptyList(),
                isLoading = false,
                isHistoryVisible = showHistory,
                isTracksVisible = if (preserveSearchResults) currentState.isTracksVisible else false,
                isEmptyStateVisible = if (preserveSearchResults) currentState.isEmptyStateVisible else false,
                isErrorStateVisible = if (preserveSearchResults) currentState.isErrorStateVisible else false
            )
        }
    }

    private inline fun updateState(transform: SearchState.() -> SearchState) {
        val currentState = _state.value ?: SearchState()
        _state.value = currentState.transform()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}
