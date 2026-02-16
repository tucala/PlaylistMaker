package com.tuca.playlistmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.text.Editable
import androidx.core.content.ContextCompat
import android.text.TextWatcher
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.appbar.MaterialToolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView



class SearchActivity : AppCompatActivity() {
    data class Track(val trackName: String,
                     val artistName: String,
                     val trackTime: String,
                     val artworkUrl100: String) // data class
    private val mockTracks: ArrayList<Track> = arrayListOf(
        Track(
            "Smells Like Teen Spirit",
            "Nirvana",
            "5:01",
            "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            "Billie Jean",
            "Michael Jackson",
            "4:35",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"
        ),
        Track(
            "Stayin' Alive",
            "Bee Gees",
            "4:10",
            "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"
        ),
        Track(
            "Whole Lotta Love",
            "Led Zeppelin",
            "5:33",
            "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"
        ),
        Track(
            "Sweet Child O'Mine",
            "Guns N' Roses",
            "5:03",
            "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"
        )
    ) // data track

    private lateinit var editTextSearch: EditText
    private var searchText: String = ""

    companion object {
        private const val KEY_SEARCH_TEXT = "KEY_SEARCH_TEXT"
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchAdapter: TrackAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarTop)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        editTextSearch = findViewById(R.id.editTextSearch)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchAdapter = TrackAdapter(mockTracks)
        recyclerView.adapter = searchAdapter
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchText = s?.toString() ?: ""

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

        val editTextSearch = findViewById<EditText>(R.id.editTextSearch)
        val clearIcon = findViewById<ImageView>(R.id.clearIcon)

        editTextSearch.doAfterTextChanged { text ->
            clearIcon.isVisible = !text.isNullOrEmpty()
        }

        clearIcon.setOnClickListener {
            editTextSearch.setText("")
        }
    }


     fun afterTextChanged(s: Editable?) {
        searchText = s?.toString() ?: ""

        val showClear = searchText.isNotEmpty()
        editTextSearch.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(this@SearchActivity, R.drawable.ic_search),
            null,
            if (showClear) ContextCompat.getDrawable(this@SearchActivity, R.drawable.ic_cleartext) else null,
            null
        )

        // Фильтр
        val filteredTracks = mockTracks.filter { track ->
            track.trackName.contains(searchText, ignoreCase = true) ||
                    track.artistName.contains(searchText, ignoreCase = true)
        }
        searchAdapter.updateTracks(filteredTracks)
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
}
