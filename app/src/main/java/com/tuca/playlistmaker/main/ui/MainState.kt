package com.tuca.playlistmaker.main.ui

sealed interface MainState {
    object Content : MainState
    object NavigateToSearch : MainState
    object NavigateToLibrary : MainState
    object NavigateToSettings : MainState
}