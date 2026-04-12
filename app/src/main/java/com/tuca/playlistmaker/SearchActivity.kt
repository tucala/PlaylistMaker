package com.tuca.playlistmaker

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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private lateinit var editTextSearch: EditText
    private lateinit var trackNotFound: View
    private lateinit var internetTrouble: View
    private lateinit var historyLayout: View
    private lateinit var historyManager: SearchHistory
    private lateinit var historyRecycler: RecyclerView
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var progressBar: ProgressBar

    private lateinit var reloadButton: View
    private var searchText: String = ""
    private var activeSearchQuery: String = ""

    private val searchHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var searchRunnable: Runnable = Runnable { }
    private lateinit var api: ITunesApi
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())



    companion object {
        private const val KEY_SEARCH_TEXT = "KEY_SEARCH_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L

    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: TrackAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        historyLayout = findViewById(R.id.historyLayout)
        historyRecycler = findViewById(R.id.trackHistoryList)
        trackNotFound = findViewById(R.id.track_not_found)
        internetTrouble = findViewById(R.id.inet_trouble_retry)
        reloadButton = findViewById(R.id.reloadButton)
        progressBar = findViewById(R.id.pbSearch)
        trackNotFound.isVisible = false
        internetTrouble.isVisible = false
        progressBar.isVisible = false

        historyManager = SearchHistory(
            getSharedPreferences("settings", MODE_PRIVATE)
        )
        historyAdapter = TrackAdapter(arrayListOf()) { track ->
            if (clickDebounce()) {
                historyManager.addTrack(track)
                openPlayer(track)
            }
        }
        historyRecycler.layoutManager = LinearLayoutManager(this)
        historyRecycler.adapter = historyAdapter
        historyLayout = findViewById(R.id.historyLayout)
        historyLayout.visibility = View.GONE
        val clearHistoryButton = findViewById<Button>(R.id.clearHistoryButton)
        clearHistoryButton.setOnClickListener {
            historyManager.clear()
            historyAdapter.updateTracks(emptyList())
            showHistory()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarTop)
        toolbar.setNavigationOnClickListener {
            finish()
        }





        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ITunesApi::class.java)

        editTextSearch = findViewById(R.id.editTextSearch)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchAdapter = TrackAdapter(arrayListOf()) { track ->
            if (clickDebounce()) {
                historyManager.addTrack(track)
                openPlayer(track)
            }
        }
        recyclerView.adapter = searchAdapter
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchText = s?.toString() ?: ""
                searchHandler.removeCallbacks(searchRunnable)
                progressBar.isVisible = false
                if (searchText.isNotEmpty()) {
                    historyLayout.visibility = View.GONE
                    historyRecycler.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    trackNotFound.visibility = View.GONE
                    internetTrouble.visibility = View.GONE
                    searchDebounce()
                } else {
                    searchAdapter.updateTracks(emptyList())
                    activeSearchQuery = ""
                    showHistory()
                }

                val showClear = searchText.isNotEmpty()
                editTextSearch.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(this@SearchActivity, R.drawable.ic_search),
                    null,
                    if (showClear)
                        ContextCompat.getDrawable(
                            this@SearchActivity,
                            R.drawable.ic_cleartext
                        )
                    else null,
                    null
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val clearIcon = findViewById<ImageView>(R.id.clearIcon)

        editTextSearch.doAfterTextChanged { text ->
            clearIcon.isVisible = !text.isNullOrEmpty()
        }

        reloadButton.setOnClickListener {
            performSearch(editTextSearch.text.toString())
        }

        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                performSearch(editTextSearch.text.toString())
                true
            } else {
                false
            }
        }

        clearIcon.setOnClickListener {
            editTextSearch.setText("")
            searchAdapter.updateTracks(emptyList())
            trackNotFound.visibility = View.GONE
            internetTrouble.visibility = View.GONE
            progressBar.isVisible = false
            showHistory()
        }
        showHistory()
    }
    private fun showState(
        showList: Boolean = false,
        showNotFound: Boolean = false,
        showError: Boolean = false,
        showLoading: Boolean = false
    ) {
        progressBar.visibility = if (showLoading) View.VISIBLE else View.GONE
        recyclerView.visibility = if (showList) View.VISIBLE else View.GONE
        if (showList) {
            historyLayout.visibility = View.GONE
            historyRecycler.visibility = View.GONE
        }

        trackNotFound.visibility = if (showNotFound) View.VISIBLE else View.GONE
        internetTrouble.visibility = if (showError) View.VISIBLE else View.GONE
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restored = savedInstanceState.getString(KEY_SEARCH_TEXT, "")
        searchText = restored
        editTextSearch.setText(restored)
    }
    private fun showHistoryIfExists() {
        val history = historyManager.getHistory()
        if (history.isNotEmpty()) {
            historyLayout.visibility = View.VISIBLE
        } else {
            historyLayout.visibility = View.GONE
        }
    }

    private fun openPlayer(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(PlayerActivity.EXTRA_TRACK, track)
        startActivity(intent)
    }
    private fun showHistory() {
        val history = historyManager.getHistory()

        if (history.isNotEmpty()) {
            historyLayout.visibility = View.VISIBLE
            historyRecycler.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            historyAdapter.updateTracks(history)
        } else {
            historyLayout.visibility = View.GONE
            historyRecycler.visibility = View.GONE
        }
    }
    private fun searchDebounce() {
        searchHandler.removeCallbacks(searchRunnable)
        searchRunnable = Runnable { performSearch(searchText) }
        searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    private fun performSearch(query: String) {
        if (query.isEmpty()) return

        activeSearchQuery = query
        showState(showList = false, showError = false, showNotFound = false, showLoading = true)

        api.search(query).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (query != searchText || query != activeSearchQuery) return
                val tracks = response.body()?.results ?: emptyList()
                searchAdapter.updateTracks(tracks)

                if (tracks.isEmpty()) {
                    showState(showNotFound = true)
                } else {
                    showState(showList = true)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                if (query != searchText || query != activeSearchQuery) return
                searchAdapter.updateTracks(emptyList())
                showState(showError = true)
            }
        })
    }
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}
