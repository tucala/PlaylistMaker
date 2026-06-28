package com.tuca.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.tuca.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var themeSwitcher: SwitchCompat
    private lateinit var shareApp: TextView
    private lateinit var textSupport: TextView
    private lateinit var userAccept: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        themeSwitcher = view.findViewById(R.id.switchDarkMode)
        shareApp = view.findViewById(R.id.shareApp)
        textSupport = view.findViewById(R.id.textSupport)
        userAccept = view.findViewById(R.id.userAccept)

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeChanged(isChecked)
        }
        shareApp.setOnClickListener { viewModel.onShareClicked() }
        textSupport.setOnClickListener { viewModel.onSupportClicked() }
        userAccept.setOnClickListener { viewModel.onTermsClicked() }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: SettingsState) {
        when (state) {
            is SettingsState.Content -> {
                if (themeSwitcher.isChecked != state.isDarkModeEnabled) {
                    themeSwitcher.isChecked = state.isDarkModeEnabled
                }
                applyTheme(state.isDarkModeEnabled)
            }
            is SettingsState.ShareApp -> {
                shareApp()
                viewModel.onActionHandled()
            }
            is SettingsState.ContactSupport -> {
                contactSupport()
                viewModel.onActionHandled()
            }
            is SettingsState.OpenTerms -> {
                openTerms()
                viewModel.onActionHandled()
            }
        }
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.shareAPP))
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.textShareSettings)))
    }

    private fun contactSupport() {
        val mailto = "mailto:${getString(R.string.mailTo)}" +
                "?subject=${Uri.encode(getString(R.string.mailSendTheme))}" +
                "&body=${Uri.encode(getString(R.string.mailSendBody))}"
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse(mailto))
        startActivity(emailIntent)
    }

    private fun openTerms() {
        val url = getString(R.string.userAcceptUrl)
        val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(browserIntent)
    }
}
