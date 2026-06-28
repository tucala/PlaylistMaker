package com.tuca.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar
import com.tuca.playlistmaker.R
import com.tuca.playlistmaker.player.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private lateinit var currentTrack: Track
    private val viewModel: PlayerViewModel by viewModel { parametersOf(currentTrack) }
    private lateinit var playButton: ImageButton
    private lateinit var playedTime: TextView
    private lateinit var trackNameText: TextView
    private lateinit var artistNameText: TextView
    private lateinit var playerImage: ImageView
    private lateinit var infoRecycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val track = arguments?.getSerializable("EXTRA_TRACK") as? Track
        if (track == null) {
            findNavController().navigateUp()
            return
        }
        currentTrack = track
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupToolbar(view)
        bindTrack(currentTrack)
        setupListeners()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun initViews(view: View) {
        playButton = view.findViewById(R.id.playButton)
        playedTime = view.findViewById(R.id.playedTime)
        trackNameText = view.findViewById(R.id.trackName)
        artistNameText = view.findViewById(R.id.artistName)
        playerImage = view.findViewById(R.id.playerImage)
        infoRecycler = view.findViewById(R.id.trackAddInfo)
    }

    private fun setupToolbar(view: View) {
        view.findViewById<MaterialToolbar>(R.id.toolbarTop).setNavigationOnClickListener {
            findNavController().navigateUp()
        }
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

        infoRecycler.layoutManager = LinearLayoutManager(requireContext())
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
