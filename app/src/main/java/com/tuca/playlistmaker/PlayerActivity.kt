package com.tuca.playlistmaker

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
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.MaterialToolbar
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TRACK = "extra_track"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
    private lateinit var play: ImageButton
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateTimerRunnable: Runnable
    private lateinit var playedTime: TextView

    private var mediaPlayer = MediaPlayer()
    private var track: Track? = null
    private var playerState = STATE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        play = findViewById(R.id.playButton)
        play.isEnabled = false
        play.setOnClickListener {
            playbackControl()
        }
        updateTimerRunnable = Runnable { updateTimer() }
        playedTime = findViewById(R.id.playedTime)


        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarTop)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        android.util.Log.d("TRACK_DEBUG", "Track URL: ${track?.previewUrl}") //testURL
        val track = intent.getSerializableExtra(EXTRA_TRACK) as? Track
        if (track == null) {
            finish()
            return
        }

        val playerImage = findViewById<ImageView>(R.id.playerImage)
        val trackName = findViewById<TextView>(R.id.trackName)
        val artistName = findViewById<TextView>(R.id.artistName)
        val infoRecycler = findViewById<RecyclerView>(R.id.trackAddInfo)

        trackName.text = track.trackName
        artistName.text = track.artistName

        val cornerRadius = resources.getDimensionPixelSize(R.dimen.playerCoverRadius)
        val options = RequestOptions()
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .transform(RoundedCorners(cornerRadius))

        Glide.with(this)
            .load(track.getCoverArtwork())
            .apply(options)
            .into(playerImage)

        infoRecycler.layoutManager = LinearLayoutManager(this)
        infoRecycler.adapter = AdditionalInfoAdapter(buildAdditionalInfo(track))
        preparePlayer(track.previewUrl)
    }

    private fun buildAdditionalInfo(track: Track): List<AdditionalInfoItem> {
        val items = mutableListOf<AdditionalInfoItem>()

        if (track.trackTimeMillis > 0) {
            items.add(
                AdditionalInfoItem(
                    getString(R.string.detail_duration),
                    track.trackTime
                )
            )
        }

        track.collectionName?.takeIf { it.isNotBlank() }?.let { value ->
            items.add(AdditionalInfoItem(getString(R.string.detail_album), value))
        }

        track.releaseDate
            ?.take(4)
            ?.takeIf { it.isNotBlank() }
            ?.let { value ->
                items.add(AdditionalInfoItem(getString(R.string.detail_year), value))
            }

        track.primaryGenreName?.takeIf { it.isNotBlank() }?.let { value ->
            items.add(AdditionalInfoItem(getString(R.string.detail_genre), value))
        }

        track.country?.takeIf { it.isNotBlank() }?.let { value ->
            items.add(AdditionalInfoItem(getString(R.string.detail_country), value))
        }

        return items

    }
    private fun updateTimer() {
        val currentTime = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(mediaPlayer.currentPosition)
        playedTime.text = currentTime
        handler.postDelayed(updateTimerRunnable, 500L)
    } // время проигрывания
    private fun preparePlayer(previewUrl: String?) {
        if (previewUrl.isNullOrEmpty()) {
            play.isEnabled = false
            return
        }
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
            play.setImageResource(R.drawable.play_button)
            handler.removeCallbacks(updateTimerRunnable)
            playedTime.text = "00:00"
        }
        mediaPlayer.setOnCompletionListener {
            handler.removeCallbacks(updateTimerRunnable)
            playerState = STATE_PREPARED
            play.setImageResource(R.drawable.play_button)
            playedTime.text = "00:00"
        }
    }
    private fun startPlayer() {
        mediaPlayer.start()
        play.setImageResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
        handler.post(updateTimerRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        play.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED
        handler.removeCallbacks(updateTimerRunnable)
    }
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

}
