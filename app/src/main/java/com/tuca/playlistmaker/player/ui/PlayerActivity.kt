package com.tuca.playlistmaker.player.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.tuca.playlistmaker.R
import com.tuca.playlistmaker.creator.Creator
import com.tuca.playlistmaker.player.domain.models.Track

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TRACK = "EXTRA_TRACK"
    }

    private lateinit var viewModel: PlayerViewModel
    private lateinit var playButton: ImageButton
    private lateinit var playedTime: TextView
    private lateinit var trackNameText: TextView
    private lateinit var artistNameText: TextView
    private lateinit var playerImage: ImageView
    private lateinit var infoRecycler: RecyclerView

    private var track: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        track = intent.getSerializableExtra(EXTRA_TRACK) as? Track
        if (track == null) {
            finish()
            return
        }

        val currentTrack = track ?: return
        val audioPlayerInteractor = Creator.provideAudioPlayerInteractor()
        val zeroTimeText = getString(R.string.zeroTime)
        viewModel = ViewModelProvider(
            this,
            PlayerViewModelFactory(currentTrack, audioPlayerInteractor, zeroTimeText)
        )[PlayerViewModel::class.java]

        initViews()
        setupToolbar()
        bindTrack(currentTrack)
        setupListeners()

        viewModel.state.observe(this) { state ->
            render(state)
        }
    }

    private fun initViews() {
        playButton = findViewById(R.id.playButton)
        playedTime = findViewById(R.id.playedTime)
        trackNameText = findViewById(R.id.trackName)
        artistNameText = findViewById(R.id.artistName)
        playerImage = findViewById(R.id.playerImage)
        infoRecycler = findViewById(R.id.trackAddInfo)
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.toolbarTop).setNavigationOnClickListener { finish() }
    }

    private fun bindTrack(currentTrack: Track) {
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
    }

    private fun setupListeners() {
        playButton.setOnClickListener {
            viewModel.onPlayClicked()
        }
    }

    private fun render(state: PlayerState) {
        playedTime.text = state.currentTimeText
        playButton.isEnabled = state.isPlayButtonEnabled
        playButton.setImageResource(
            if (state.isPlaying) R.drawable.pause_button else R.drawable.play_button
        )
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPauseFromUi()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onClearedFromUi()
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
