package com.tuca.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.net.toUri

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
        val backButton = findViewById<ImageView>(R.id.btnBack)
        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            finish()
        }

        val shareApp = findViewById<TextView>(R.id.shareApp)
        shareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareAPP))
            startActivity(Intent.createChooser(shareIntent, "share App"))
        }

        val textSupport = findViewById<TextView>(R.id.textSupport)
        textSupport.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "message/rfc822"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mailTo)))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mailSendTheme))
            emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mailSendBody))
            startActivity(Intent.createChooser(emailIntent, "send email"))
        }

        val userAccept = findViewById<TextView>(R.id.userAccept)
        userAccept.setOnClickListener {
            val url = getString(R.string.userAcceptUrl)
            val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(browserIntent)
        }
    }
}