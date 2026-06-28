package com.tuca.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.tuca.playlistmaker.R
import com.tuca.playlistmaker.databinding.FragmentPlayerBinding
import com.tuca.playlistmaker.player.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : Fragment() {

    private lateinit var currentTrack: Track
    private val viewModel: PlayerViewModel by viewModel { parametersOf(currentTrack) }

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

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
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        bindTrack(currentTrack)
        setupListeners()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun setupToolbar() {
        binding.toolbarTop.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun bindTrack(currentTrack: Track) {
        binding.trackName.text = currentTrack.trackName
        binding.artistName.text = currentTrack.artistName

        val cornerRadius = resources.getDimensionPixelSize(R.dimen.playerCoverRadius)
        Glide.with(this)
            .load(currentTrack.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))
            .into(binding.playerImage)

        binding.trackAddInfo.layoutManager = LinearLayoutManager(requireContext())
        binding.trackAddInfo.adapter = AdditionalInfoAdapter(buildAdditionalInfo(currentTrack))
    }

    private fun setupListeners() {
        binding.playButton.setOnClickListener {
            viewModel.onPlayClicked()
        }
    }

    private fun render(state: PlayerState) {
        binding.playedTime.text = state.currentTimeText
        binding.playButton.isEnabled = state.isPlayButtonEnabled
        binding.playButton.setImageResource(
            if (state.isPlaying) R.drawable.pause_button else R.drawable.play_button
        )
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPauseFromUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
