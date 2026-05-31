package com.tuca.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tuca.playlistmaker.settings.domain.api.ThemeSettingsInteractor

class SettingsViewModel(
    private val themeSettingsInteractor: ThemeSettingsInteractor
) : ViewModel() {

    private val _state = MutableLiveData<SettingsState>()
    val state: LiveData<SettingsState> get() = _state

    init {
        val isDark = themeSettingsInteractor.isDarkThemeEnabled()
        _state.value = SettingsState.Content(isDark)
    }

    fun onThemeChanged(isChecked: Boolean) {
        themeSettingsInteractor.setDarkThemeEnabled(isChecked)
        _state.value = SettingsState.Content(isChecked)
    }

    fun onShareClicked() { _state.value = SettingsState.ShareApp }
    fun onSupportClicked() { _state.value = SettingsState.ContactSupport }
    fun onTermsClicked() { _state.value = SettingsState.OpenTerms }

    fun onActionHandled() {
        val isDark = themeSettingsInteractor.isDarkThemeEnabled()
        _state.value = SettingsState.Content(isDark)
    }
}
