package com.tuca.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuca.playlistmaker.R
import com.tuca.playlistmaker.databinding.FragmentFavoritesBinding
import com.tuca.playlistmaker.player.domain.models.Track
import com.tuca.playlistmaker.search.ui.track.TrackAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    companion object {
        fun newInstance() = FavoritesFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val viewModel: FavoritesViewModel by viewModel()
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TrackAdapter
    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    override fun onResume() {
        super.onResume()
        isClickAllowed = true
        viewModel.fillData()
    }

    private fun setupAdapter() {
        adapter = TrackAdapter(arrayListOf()) { track ->
            if (clickDebounce()) {
                openPlayer(track)
            }
        }
        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesRecyclerView.adapter = adapter
    }

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Empty -> {
                binding.favoritesRecyclerView.isVisible = false
                binding.emptyFavoritesLayout.isVisible = true
            }
            is FavoritesState.Content -> {
                binding.emptyFavoritesLayout.isVisible = false
                binding.favoritesRecyclerView.isVisible = true
                adapter.updateTracks(state.tracks)
            }
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun openPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_libraryFragment_to_playerFragment,
            Bundle().apply { putSerializable("EXTRA_TRACK", track) }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
