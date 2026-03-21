package com.tuca.playlistmaker

import android.os.Bundle
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

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TRACK = "extra_track"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarTop)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val track = intent.getSerializableExtra(EXTRA_TRACK) as? Track
        if (track == null) {
            finish()
            return
        }

        val playerImage = findViewById<ImageView>(R.id.playerImage)
        val trackName = findViewById<TextView>(R.id.trackName)
        val artistName = findViewById<TextView>(R.id.artistName)
        val playedTime = findViewById<TextView>(R.id.playedTime)
        val infoRecycler = findViewById<RecyclerView>(R.id.trackAddInfo)

        trackName.text = track.trackName
        artistName.text = track.artistName
        playedTime.text = track.trackTime

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
}
