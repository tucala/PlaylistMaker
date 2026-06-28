package com.tuca.playlistmaker.search.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tuca.playlistmaker.R
import com.tuca.playlistmaker.databinding.FragmentSearchBinding
import com.tuca.playlistmaker.player.domain.models.Track
import com.tuca.playlistmaker.search.ui.track.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchAdapter: TrackAdapter

    private var isClickAllowed = true
    private var isProgrammaticTextChange = false

    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHistoryAdapter()
        setupSearchAdapter()
        setupSearchEditText()
        setupActions()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun setupHistoryAdapter() {
        historyAdapter = TrackAdapter(arrayListOf()) { track ->
            if (clickDebounce()) {
                viewModel.onTrackClicked(track)
                openPlayer(track)
            }
        }
        binding.historyLayout.trackHistoryList.layoutManager = LinearLayoutManager(requireContext())
        binding.historyLayout.trackHistoryList.adapter = historyAdapter

        binding.historyLayout.clearHistoryButton.setOnClickListener {
            viewModel.onClearHistoryClicked()
        }
    }

    private fun setupSearchAdapter() {
        searchAdapter = TrackAdapter(arrayListOf()) { track ->
            if (clickDebounce()) {
                viewModel.onTrackClicked(track)
                openPlayer(track)
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = searchAdapter
    }

    private fun setupSearchEditText() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isProgrammaticTextChange) return
                viewModel.onQueryChanged(s?.toString().orEmpty())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })
    }

    private fun setupActions() {
        binding.clearIcon.setOnClickListener {
            binding.editTextSearch.setText("")
        }

        binding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onSearchAction()
                true
            } else {
                false
            }
        }

        binding.inetTroubleRetry.reloadButton.setOnClickListener {
            viewModel.onRetryClicked()
        }
    }

    private fun render(state: SearchState) {
        if (binding.editTextSearch.text?.toString() != state.query) {
            isProgrammaticTextChange = true
            binding.editTextSearch.setText(state.query)
            binding.editTextSearch.setSelection(state.query.length)
            isProgrammaticTextChange = false
        }

        binding.clearIcon.isVisible = state.query.isNotEmpty()
        binding.pbSearch.isVisible = state.isLoading
        binding.historyLayout.root.isVisible = state.isHistoryVisible
        binding.recyclerView.isVisible = state.isTracksVisible
        binding.trackNotFound.root.isVisible = state.isEmptyStateVisible
        binding.inetTroubleRetry.root.isVisible = state.isErrorStateVisible

        historyAdapter.updateTracks(state.history)
        searchAdapter.updateTracks(state.tracks)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun openPlayer(track: Track) {
        findNavController().navigate(
            R.id.action_searchFragment_to_playerFragment,
            Bundle().apply { putSerializable("EXTRA_TRACK", track) }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
