package com.tuca.playlistmaker.library.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.tuca.playlistmaker.R
import com.tuca.playlistmaker.library.domain.models.Playlist
import java.io.File
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : NewPlaylistFragment() {

    companion object {
        const val ARG_PLAYLIST = "EDIT_PLAYLIST"
    }

    override val viewModel: EditPlaylistViewModel by viewModel()

    private val playlist: Playlist? by lazy {
        @Suppress("DEPRECATION")
        arguments?.getSerializable(ARG_PLAYLIST) as? Playlist
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarTop.title = getString(R.string.edit_playlist_title)
        binding.createButton.text = getString(R.string.save_playlist_button)

        playlist?.let { pl ->
            viewModel.initPlaylist(pl)
            binding.playlistNameEditText.setText(pl.name)
            binding.playlistDescriptionEditText.setText(pl.description ?: "")
            if (!pl.coverPath.isNullOrEmpty()) {
                val cornerRadius = resources.getDimensionPixelSize(R.dimen.playerCoverRadius)
                Glide.with(this)
                    .load(File(pl.coverPath))
                    .transform(
                        CenterCrop(),
                        RoundedCorners(cornerRadius)
                    )
                    .placeholder(R.drawable.ic_new_playlist)
                    .error(R.drawable.ic_new_playlist)
                    .into(binding.playlistCoverImage)
            } else {
                binding.playlistCoverImage.setImageResource(R.drawable.ic_new_playlist)
            }
        }
    }

    override fun handleBackPress() {
        findNavController().navigateUp()
    }

    override fun onSaveButtonClicked(name: String, description: String?, coverUri: Uri?) {
        viewModel.updatePlaylist(name, description, coverUri)
    }

    override fun observeViewModel() {
        viewModel.playlistCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }
}
