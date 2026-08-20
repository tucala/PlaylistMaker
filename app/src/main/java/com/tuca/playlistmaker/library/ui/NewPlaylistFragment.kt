package com.tuca.playlistmaker.library.ui

import android.net.Uri
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tuca.playlistmaker.R
import com.tuca.playlistmaker.databinding.FragmentNewplaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {

    private val viewModel: NewPlaylistViewModel by viewModel()

    private var _binding: FragmentNewplaylistBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            val cornerRadius = resources.getDimensionPixelSize(R.dimen.playerCoverRadius)
            Glide.with(this)
                .load(uri)
                .transform(CenterCrop(), RoundedCorners(cornerRadius))
                .into(binding.playlistCoverImage)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewplaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupBackNavigation()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.toolbarTop.setNavigationOnClickListener {
            handleBackPress()
        }

        binding.playlistCoverImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.playlistNameEditText.doOnTextChanged { s, _, _, _ ->
            binding.createButton.isEnabled = !s.isNullOrBlank()
        }

        binding.createButton.setOnClickListener {
            val name = binding.playlistNameEditText.text?.toString()?.trim() ?: ""
            val description = binding.playlistDescriptionEditText.text?.toString()?.trim()
            viewModel.createPlaylist(name, description, selectedImageUri)
        }
    }

    private fun setupBackNavigation() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPress()
                }
            }
        )
    }

    private fun handleBackPress() {
        val hasCover = selectedImageUri != null
        val hasName = !binding.playlistNameEditText.text.isNullOrBlank()
        val hasDescription = !binding.playlistDescriptionEditText.text.isNullOrBlank()

        if (hasCover || hasName || hasDescription) {
            showConfirmDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.finish_playlist_creation_title)
            .setMessage(R.string.finish_playlist_creation_message)
            .setNeutralButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.finish) { dialog, _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .show()
    }

    private fun observeViewModel() {
        viewModel.playlistCreated.observe(viewLifecycleOwner) { playlistName ->
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_created_toast, playlistName),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
