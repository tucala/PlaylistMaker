package com.tuca.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesViewModel : ViewModel() {
    private val _state = MutableLiveData<FavoritesState>(FavoritesState.Empty)
    val state: LiveData<FavoritesState> get() = _state
}
