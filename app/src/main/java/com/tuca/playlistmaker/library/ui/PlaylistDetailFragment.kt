package com.tuca.playlistmaker.library.ui

import android.content.Intent
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
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tuca.playlistmaker.R
import com.tuca.playlistmaker.databinding.FragmentPlaylistDetailBinding
import com.tuca.playlistmaker.player.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistDetailFragment : Fragment() {

    companion object {
        const val ARG_PLAYLIST_ID = "PLAYLIST_ID"
    }

    private val playlistId: Int by lazy {
        arguments?.getInt(ARG_PLAYLIST_ID, -1) ?: -1
    }

    private val viewModel: PlaylistDetailViewModel by viewModel { parametersOf(playlistId) }

    private var _binding: FragmentPlaylistDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var tracksAdapter: PlaylistTrackAdapter
    private lateinit var tracksBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupTracksBottomSheet()
        setupMenuBottomSheet()
        setupListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylist()
    }

    private fun setupToolbar() {
        binding.toolbarTop.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupTracksBottomSheet() {
        tracksBottomSheetBehavior = BottomSheetBehavior.from(binding.tracksBottomSheet).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            isHideable = false
        }
        tracksAdapter = PlaylistTrackAdapter(
            emptyList(),
            onTrackClick = { track -> navigateToPlayer(track) },
            onTrackLongClick = { track -> showDeleteTrackDialog(track) }
        )
        binding.rvTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTracks.adapter = tracksAdapter
    }

    private fun setupMenuBottomSheet() {
        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
            isHideable = true
        }
        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val b = _binding ?: return
                b.dimOverlay.isVisible = newState != BottomSheetBehavior.STATE_HIDDEN
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val b = _binding ?: return
                if (slideOffset >= -1f) {
                    b.dimOverlay.alpha = (slideOffset + 1f) / 2f
                }
            }
        })
    }

    private fun setupListeners() {
        binding.btnShare.setOnClickListener { handleShare() }
        binding.btnMenu.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.dimOverlay.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        binding.menuItemShare.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            handleShare()
        }
        binding.menuItemEdit.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val playlist = viewModel.playlist.value ?: return@setOnClickListener
            val bundle = android.os.Bundle().apply {
                putSerializable(EditPlaylistFragment.ARG_PLAYLIST, playlist)
            }
            findNavController().navigate(R.id.action_playlistDetailFragment_to_editPlaylistFragment, bundle)
        }
        binding.menuItemDelete.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            binding.tvPlaylistName.text = playlist.name
            if (!playlist.description.isNullOrBlank()) {
                binding.tvPlaylistDescription.isVisible = true
                binding.tvPlaylistDescription.text = playlist.description
            } else {
                binding.tvPlaylistDescription.isVisible = false
            }
            val count = playlist.tracksCount
            updatePlaylistStats(count)
            val cornerRadius = resources.getDimensionPixelSize(R.dimen.playerCoverRadius)
            if (!playlist.coverPath.isNullOrEmpty()) {
                Glide.with(this)
                    .load(File(playlist.coverPath))
                    .transform(CenterCrop(), RoundedCorners(cornerRadius))
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .into(binding.ivPlaylistCover)
            } else {
                binding.ivPlaylistCover.setImageResource(R.drawable.ic_placeholder)
            }
            binding.menuPlaylistInfo.tvPlaylistName.text = playlist.name
            binding.menuPlaylistInfo.tvPlaylistTracksCount.text = resources.getQuantityString(
                R.plurals.tracks_count,
                count,
                count
            )
            if (!playlist.coverPath.isNullOrEmpty()) {
                Glide.with(this)
                    .load(File(playlist.coverPath))
                    .transform(CenterCrop(), RoundedCorners(resources.getDimensionPixelSize(R.dimen.cover_corner_radius)))
                    .placeholder(R.drawable.ic_placeholder)
                    .into(binding.menuPlaylistInfo.ivPlaylistCover)
            } else {
                binding.menuPlaylistInfo.ivPlaylistCover.setImageResource(R.drawable.ic_placeholder)
            }
        }
        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            tracksAdapter.updateTracks(tracks)
            binding.tvEmptyTracks.isVisible = tracks.isEmpty()
            binding.rvTracks.isVisible = tracks.isNotEmpty()
            updatePlaylistStats(viewModel.playlist.value?.tracksCount ?: 0)
        }
        viewModel.playlistDeleted.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    private fun navigateToPlayer(track: Track) {
        val bundle = android.os.Bundle().apply {
            putSerializable("EXTRA_TRACK", track)
        }
        findNavController().navigate(R.id.action_playlistDetailFragment_to_playerFragment, bundle)
    }

    private fun handleShare() {
        val shareText = viewModel.buildShareText()
        if (shareText == null) {
            Toast.makeText(requireContext(), getString(R.string.no_tracks_to_share), Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(intent, null))
        }
    }

    private fun updatePlaylistStats(trackCount: Int) {
        val countText = resources.getQuantityString(R.plurals.tracks_count, trackCount, trackCount)
        binding.tvPlaylistDuration.text = "${viewModel.getTotalDurationText()} • $countText"
    }

    private fun showDeleteTrackDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.delete_track_title)
            .setNegativeButton(R.string.delete_track_no) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.delete_track_yes) { dialog, _ ->
                dialog.dismiss()
                viewModel.removeTrack(track)
            }
            .show()
    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_playlist_title)
            .setMessage(R.string.delete_playlist_message)
            .setNegativeButton(R.string.delete_playlist_cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.delete_playlist_confirm) { dialog, _ ->
                dialog.dismiss()
                viewModel.deletePlaylist()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
