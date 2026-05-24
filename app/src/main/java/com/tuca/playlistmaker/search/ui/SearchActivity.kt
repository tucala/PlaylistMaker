package com.tuca.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.tuca.playlistmaker.R
import com.tuca.playlistmaker.creator.Creator
import com.tuca.playlistmaker.player.domain.models.Track
import com.tuca.playlistmaker.player.ui.PlayerActivity
import com.tuca.playlistmaker.search.ui.track.TrackAdapter

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        val searchInteractor = Creator.provideSearchInteractor(applicationContext)
        viewModel = ViewModelProvider(
            this,
            SearchViewModelFactory(searchInteractor)
        )[SearchViewModel::class.java]

        initViews()
        setupToolbar()
        setupHistoryAdapter()
        setupSearchAdapter()
        setupSearchEditText()
        setupActions()

        viewModel.state.observe(this) { state ->
            render(state)
        }
    }

    private fun initViews() {
        historyLayout = findViewById(R.id.historyLayout)
        historyRecycler = findViewById(R.id.trackHistoryList)
        trackNotFound = findViewById(R.id.track_not_found)
        internetTrouble = findViewById(R.id.inet_trouble_retry)
        reloadButton = findViewById(R.id.reloadButton)
        progressBar = findViewById(R.id.pbSearch)
        recyclerView = findViewById(R.id.recyclerView)
        editTextSearch = findViewById(R.id.editTextSearch)
        clearIcon = findViewById(R.id.clearIcon)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupToolbar() {
        findViewById<MaterialToolbar>(R.id.toolbarTop).setNavigationOnClickListener { finish() }
    }

    private fun setupHistoryAdapter() {
        historyAdapter = TrackAdapter(arrayListOf()) { track ->
            if (clickDebounce()) {
                viewModel.onTrackClicked(track)
                openPlayer(track)
            }
        }
        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyRecycler.adapter = historyAdapter

        findViewById<Button>(R.id.clearHistoryButton).setOnClickListener {
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
        recyclerView.layoutManager = LinearLayoutManager(this)
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
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("EXTRA_TRACK", track)
        startActivity(intent)
    }
}
