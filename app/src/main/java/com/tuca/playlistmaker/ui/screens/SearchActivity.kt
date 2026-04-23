package com.tuca.playlistmaker.ui.screens

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.tuca.playlistmaker.Creator
import com.tuca.playlistmaker.R
import com.tuca.playlistmaker.domain.api.TrackInteractor
import com.tuca.playlistmaker.domain.models.Track
import com.tuca.playlistmaker.ui.track.SearchHistory
import com.tuca.playlistmaker.ui.track.TrackAdapter

class SearchActivity : AppCompatActivity() {

    private val trackInteractor = Creator.provideTrackInteractor()

    private lateinit var editTextSearch: EditText
    private lateinit var trackNotFound: View
    private lateinit var internetTrouble: View
    private lateinit var historyLayout: View
    private lateinit var historyManager: SearchHistory
    private lateinit var historyRecycler: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var reloadButton: View

    private var searchText: String = ""
    private var activeSearchQuery: String = ""
    private var isClickAllowed = true

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable = Runnable { performSearch(searchText) }
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val KEY_SEARCH_TEXT = "KEY_SEARCH_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        initViews()
        findViewById<MaterialToolbar>(R.id.toolbarTop).setNavigationOnClickListener { finish() }
        historyManager = SearchHistory(getSharedPreferences("settings", MODE_PRIVATE))
        setupHistoryAdapter()
        setupSearchAdapter()
        setupSearchEditText()
        reloadButton.setOnClickListener { performSearch(searchText) }
        showHistory()
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupHistoryAdapter() {
        historyAdapter = TrackAdapter(arrayListOf()) { track ->
            if (clickDebounce()) {
                historyManager.addTrack(track)
                openPlayer(track)
            }
        }
        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyRecycler.adapter = historyAdapter

        findViewById<Button>(R.id.clearHistoryButton).setOnClickListener {
            historyManager.clear()
            showHistory()
        }
    }

    private fun setupSearchAdapter() {
        searchAdapter = TrackAdapter(arrayListOf()) { track ->
            if (clickDebounce()) {
                historyManager.addTrack(track)
                openPlayer(track)
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = searchAdapter
    }

    private fun setupSearchEditText() {
        val clearIcon = findViewById<ImageView>(R.id.clearIcon)

        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchText = s?.toString() ?: ""
                searchHandler.removeCallbacks(searchRunnable)

                if (searchText.isNotEmpty()) {
                    showState(showLoading = false)
                    searchDebounce()
                } else {
                    searchAdapter.updateTracks(emptyList())
                    showHistory()
                }
                clearIcon.isVisible = searchText.isNotEmpty()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        clearIcon.setOnClickListener {
            editTextSearch.setText("")
            searchAdapter.updateTracks(emptyList())
            showState()
            showHistory()
        }

        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(searchText)
                true
            } else false
        }
    }

    private fun performSearch(query: String) {
        if (query.isEmpty()) return

        activeSearchQuery = query
        showState(showLoading = true)

        trackInteractor.searchTracks(query, object : TrackInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                runOnUiThread {
                    if (query == searchText && query == activeSearchQuery) {
                        progressBar.isVisible = false
                        if (foundTracks != null) {
                            searchAdapter.updateTracks(foundTracks)
                            if (foundTracks.isEmpty()) showState(showNotFound = true)
                            else showState(showList = true)
                        } else {
                            showState(showError = true)
                        }
                    }
                }
            }
        })
    }

    private fun showState(showList: Boolean = false, showNotFound: Boolean = false, showError: Boolean = false, showLoading: Boolean = false) {
        progressBar.isVisible = showLoading
        recyclerView.isVisible = showList
        trackNotFound.isVisible = showNotFound
        internetTrouble.isVisible = showError
        if (showList || showLoading || showNotFound || showError) historyLayout.isVisible = false
    }

    private fun showHistory() {
        val history = historyManager.getHistory()
        if (searchText.isEmpty()) {
            progressBar.isVisible = false
            if (history.isNotEmpty()) {
                historyLayout.isVisible = true
                historyRecycler.isVisible = true
                recyclerView.isVisible = false
                historyAdapter.updateTracks(history)
            } else {
                historyLayout.isVisible = false
                recyclerView.isVisible = false
            }
        }
    }

    private fun searchDebounce() {
        searchHandler.removeCallbacks(searchRunnable)
        searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
        editTextSearch.setText(searchText)
    }
}