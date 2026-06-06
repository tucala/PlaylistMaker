package com.tuca.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.tuca.playlistmaker.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var themeSwitcher: SwitchCompat
    private lateinit var shareApp: TextView
    private lateinit var textSupport: TextView
    private lateinit var userAccept: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.setting)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarTop)
        toolbar.setNavigationOnClickListener { finish() }

        themeSwitcher = findViewById(R.id.switchDarkMode)
        shareApp = findViewById(R.id.shareApp)
        textSupport = findViewById(R.id.textSupport)
        userAccept = findViewById(R.id.userAccept)

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeChanged(isChecked)
        }
        shareApp.setOnClickListener { viewModel.onShareClicked() }
        textSupport.setOnClickListener { viewModel.onSupportClicked() }
        userAccept.setOnClickListener { viewModel.onTermsClicked() }

        viewModel.state.observe(this) { state ->
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
