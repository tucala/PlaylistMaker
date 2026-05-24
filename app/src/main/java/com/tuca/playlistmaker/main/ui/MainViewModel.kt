package com.tuca.playlistmaker.main.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _state = MutableLiveData<MainState>(MainState.Content)
    val state: LiveData<MainState> get() = _state

    fun onSearchClicked() {
        _state.value = MainState.NavigateToSearch
    }

    fun onLibraryClicked() {
        _state.value = MainState.NavigateToLibrary
    }

    fun onSettingsClicked() {
        _state.value = MainState.NavigateToSettings
    }

    fun onNavigationHandled() {
        _state.value = MainState.Content
    }
}