package com.tuca.playlistmaker.player.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetAdapter: PlaylistsBottomSheetAdapter

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
        setupBottomSheet()
        setupListeners()
        observeViewModel()
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

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val currentBinding = _binding ?: return
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        currentBinding.dimOverlay.isVisible = false
                    }
                    else -> {
                        currentBinding.dimOverlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val currentBinding = _binding ?: return
                currentBinding.dimOverlay.alpha = (slideOffset + 1f) / 2f
            }
        })

        bottomSheetAdapter = PlaylistsBottomSheetAdapter(emptyList()) { playlist ->
            viewModel.onPlaylistClicked(playlist)
        }
        binding.bottomSheetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.bottomSheetRecyclerView.adapter = bottomSheetAdapter
    }

    private fun setupListeners() {
        binding.playButton.setOnClickListener {
            viewModel.onPlayClicked()
        }
        binding.likeTrack.setOnClickListener {
            viewModel.onFavoriteClicked()
        }
        binding.addToPlaylist.setOnClickListener {
            viewModel.loadPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.dimOverlay.isVisible = true
        }
        binding.dimOverlay.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.newPlaylistBottomSheetButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            bottomSheetAdapter.updatePlaylists(playlists)
        }

        viewModel.playlistAdditionEvent.observe(viewLifecycleOwner) { (isAdded, playlistName) ->
            if (isAdded) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.added_to_playlist_toast, playlistName),
                    Toast.LENGTH_SHORT
                ).show()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.track_already_in_playlist_toast, playlistName),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun render(state: PlayerState) {
        binding.playedTime.text = state.currentTimeText
        binding.playButton.isEnabled = state.isPlayButtonEnabled
        binding.playButton.setImageResource(
            if (state.isPlaying) R.drawable.pause_button else R.drawable.play_button
        )
        binding.likeTrack.setImageResource(
            if (state.isFavorite) R.drawable.like_button_active else R.drawable.like_button
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
