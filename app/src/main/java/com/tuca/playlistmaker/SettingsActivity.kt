package com.tuca.playlistmaker

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

class SettingsActivity : AppCompatActivity() {

    private var darkTheme = false
    private val prefs by lazy { getSharedPreferences("settings", MODE_PRIVATE) }

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

        darkTheme = prefs.getBoolean("dark_theme", false)
        switchTheme(darkTheme)

        val themeSwitcher = findViewById<SwitchCompat>(R.id.switchDarkMode)
        themeSwitcher.isChecked = darkTheme
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            switchTheme(isChecked)
            prefs.edit().putBoolean("dark_theme", isChecked).apply()
        }

        val shareApp = findViewById<TextView>(R.id.shareApp)
        shareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.shareAPP))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.textShareSettings)))
        }

        val textSupport = findViewById<TextView>(R.id.textSupport)
        textSupport.setOnClickListener {
            val mailto = "mailto:${getString(R.string.mailTo)}" +
                    "?subject=${Uri.encode(getString(R.string.mailSendTheme))}" +
                    "&body=${Uri.encode(getString(R.string.mailSendBody))}"
            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse(mailto))
            startActivity(emailIntent)
        }

        val userAccept = findViewById<TextView>(R.id.userAccept)
        userAccept.setOnClickListener {
            val url = getString(R.string.userAcceptUrl)
            val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(browserIntent)
        }
    }

    private fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}