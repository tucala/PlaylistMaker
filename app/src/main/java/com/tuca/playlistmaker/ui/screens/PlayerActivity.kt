package com.tuca.playlistmaker.ui.screens

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.tuca.playlistmaker.R
import com.tuca.playlistmaker.domain.models.Track
import com.tuca.playlistmaker.presentation.AdditionalInfoAdapter
import com.tuca.playlistmaker.presentation.AdditionalInfoItem
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TRACK = "EXTRA_TRACK"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private lateinit var playButton: ImageButton
    private lateinit var playedTime: TextView
    private val handler = Handler(Looper.getMainLooper())

    private var track: Track? = null
    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                playedTime.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(mediaPlayer.currentPosition)
                handler.postDelayed(this, 500L)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        track = intent.getSerializableExtra(EXTRA_TRACK) as? Track
        if (track == null) {
            finish()
            return
        }

        playButton = findViewById(R.id.playButton)
        playedTime = findViewById(R.id.playedTime)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarTop)
        val playerImage = findViewById<ImageView>(R.id.playerImage)
        val trackNameText = findViewById<TextView>(R.id.trackName)
        val artistNameText = findViewById<TextView>(R.id.artistName)
        val infoRecycler = findViewById<RecyclerView>(R.id.trackAddInfo)

        toolbar.setNavigationOnClickListener { finish() }

        track?.let { currentTrack ->
            trackNameText.text = currentTrack.trackName
            artistNameText.text = currentTrack.artistName

            val cornerRadius = resources.getDimensionPixelSize(R.dimen.playerCoverRadius)
            Glide.with(this)
                .load(currentTrack.getCoverArtwork())
                .placeholder(R.drawable.ic_placeholder)
                .centerCrop()
                .transform(RoundedCorners(cornerRadius))
                .into(playerImage)

            infoRecycler.layoutManager = LinearLayoutManager(this)
            infoRecycler.adapter = AdditionalInfoAdapter(buildAdditionalInfo(currentTrack))

            preparePlayer(currentTrack.previewUrl)
        }

        playButton.setOnClickListener {
            playbackControl()
        }
    }

    private fun preparePlayer(previewUrl: String?) {
        if (previewUrl.isNullOrEmpty()) return

        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            playButton.setImageResource(R.drawable.play_button)
            handler.removeCallbacks(updateTimerRunnable)
            playedTime.text = "00:00"
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
        handler.post(updateTimerRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED
        handler.removeCallbacks(updateTimerRunnable)
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimerRunnable)
        mediaPlayer.release()
    }

    private fun buildAdditionalInfo(track: Track): List<AdditionalInfoItem> {
        return mutableListOf<AdditionalInfoItem>().apply {
            add(AdditionalInfoItem(getString(R.string.detail_duration), track.trackTime))
            track.collectionName?.takeIf { it.isNotBlank() }?.let { add(AdditionalInfoItem(getString(R.string.detail_album), it)) }
            track.releaseDate?.take(4)?.let { add(AdditionalInfoItem(getString(R.string.detail_year), it)) }
            track.primaryGenreName?.let { add(AdditionalInfoItem(getString(R.string.detail_genre), it)) }
            track.country?.let { add(AdditionalInfoItem(getString(R.string.detail_country), it)) }
        }
    }
}