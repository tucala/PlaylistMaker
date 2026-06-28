package com.tuca.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tuca.playlistmaker.R
import com.tuca.playlistmaker.player.domain.models.Track
import com.tuca.playlistmaker.search.ui.track.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var editTextSearch: EditText
    private lateinit var trackNotFound: View
    private lateinit var internetTrouble: View
    private lateinit var historyLayout: View
    private lateinit var historyRecycler: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var reloadButton: View
    private lateinit var clearIcon: ImageView

    private var isClickAllowed = true
    private var isProgrammaticTextChange = false

    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupHistoryAdapter()
        setupSearchAdapter()
        setupSearchEditText()
        setupActions()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun initViews(view: View) {
        historyLayout = view.findViewById(R.id.historyLayout)
        historyRecycler = view.findViewById(R.id.trackHistoryList)
        trackNotFound = view.findViewById(R.id.track_not_found)
        internetTrouble = view.findViewById(R.id.inet_trouble_retry)
        reloadButton = view.findViewById(R.id.reloadButton)
        progressBar = view.findViewById(R.id.pbSearch)
        recyclerView = view.findViewById(R.id.recyclerView)
        editTextSearch = view.findViewById(R.id.editTextSearch)
        clearIcon = view.findViewById(R.id.clearIcon)
    }

    private fun setupHistoryAdapter() {
        historyAdapter = TrackAdapter(arrayListOf()) { track ->
            if (clickDebounce()) {
                viewModel.onTrackClicked(track)
                openPlayer(track)
            }
        }
        historyRecycler.layoutManager = LinearLayoutManager(requireContext())
        historyRecycler.adapter = historyAdapter

        view?.findViewById<Button>(R.id.clearHistoryButton)?.setOnClickListener {
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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = searchAdapter
    }

    private fun setupSearchEditText() {
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isProgrammaticTextChange) return
                viewModel.onQueryChanged(s?.toString().orEmpty())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })
    }

    private fun setupActions() {
        clearIcon.setOnClickListener {
            editTextSearch.setText("")
        }

        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.onSearchAction()
                true
            } else {
                false
            }
        }

        reloadButton.setOnClickListener {
            viewModel.onRetryClicked()
        }
    }

    private fun render(state: SearchState) {
        if (editTextSearch.text?.toString() != state.query) {
            isProgrammaticTextChange = true
            editTextSearch.setText(state.query)
            editTextSearch.setSelection(state.query.length)
            isProgrammaticTextChange = false
        }

        clearIcon.isVisible = state.query.isNotEmpty()
        progressBar.isVisible = state.isLoading
        historyLayout.isVisible = state.isHistoryVisible
        recyclerView.isVisible = state.isTracksVisible
        trackNotFound.isVisible = state.isEmptyStateVisible
        internetTrouble.isVisible = state.isErrorStateVisible

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
}
