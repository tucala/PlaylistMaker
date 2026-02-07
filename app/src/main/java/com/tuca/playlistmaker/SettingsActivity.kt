package com.tuca.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.net.toUri
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : AppCompatActivity() {
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
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val shareApp = findViewById<TextView>(R.id.shareApp)
        shareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareAPP))
            startActivity(Intent.createChooser(shareIntent, "@string/textShareSettings"))
        }

        val textSupport = findViewById<TextView>(R.id.textSupport)
        textSupport.setOnClickListener {
            val mailto = "mailto:${getString(R.string.mailTo)}" +
                    "?subject=${Uri.encode(getString(R.string.mailSendTheme))}" +
                    "&body=${Uri.encode(getString(R.string.mailSendBody))}"
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(mailto)
            }
            startActivity(emailIntent)
        }



        val userAccept = findViewById<TextView>(R.id.userAccept)
        userAccept.setOnClickListener {
            val url = getString(R.string.userAcceptUrl)
            val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(browserIntent)
        }
    }
}