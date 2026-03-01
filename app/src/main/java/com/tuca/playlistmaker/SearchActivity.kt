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
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.appbar.MaterialToolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.collections.emptyList


class SearchActivity : AppCompatActivity() {
    data class Track(val trackName: String,
                     val artistName: String,
                     @SerializedName("trackTimeMillis")
                     val trackTimeMillis: Int,
                     val artworkUrl100: String
    ) {
        val trackTime: String
            get() = SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(trackTimeMillis.toLong())
    }

    private lateinit var editTextSearch: EditText
    private lateinit var trackNotFound: View
    private lateinit var internetTrouble: View
    private lateinit var reloadButton: View
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
        trackNotFound = findViewById(R.id.track_not_found)
        internetTrouble = findViewById(R.id.inet_trouble_retry)
        reloadButton = findViewById(R.id.reloadButton)
        trackNotFound.isVisible = false
        internetTrouble.isVisible = false


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

        val api = retrofit.create(ITunesApi::class.java)

        editTextSearch = findViewById(R.id.editTextSearch)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchAdapter = TrackAdapter(arrayListOf())
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

        val clearIcon = findViewById<ImageView>(R.id.clearIcon)

        editTextSearch.doAfterTextChanged { text ->
            clearIcon.isVisible = !text.isNullOrEmpty()
        }

        reloadButton.setOnClickListener {
            val query = editTextSearch.text.toString()
            if (query.isNotEmpty()) {
                showState(showList = false, showError = false, showNotFound = false)
                api.search(query).enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        val tracks = response.body()?.results ?: emptyList()
                        searchAdapter.updateTracks(tracks)

                        if (tracks.isEmpty()) {
                            showState(showNotFound = true)
                        } else {
                            showState(showList = true)
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showState(showError = true)
                    }
                })
            }
        }

        clearIcon.setOnClickListener {
            editTextSearch.setText("")
            searchAdapter.updateTracks(emptyList())
            trackNotFound.visibility = View.GONE
            internetTrouble.visibility = View.GONE
        }
        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                val query = editTextSearch.text.toString()

                if (query.isNotEmpty()) {
                    api.search(query).enqueue(object : Callback<TrackResponse> {
                        override fun onResponse(
                            call: Call<TrackResponse>,
                            response: Response<TrackResponse>
                        ) {
                            val tracks = response.body()?.results ?: emptyList()
                            searchAdapter.updateTracks(tracks)

                            if (tracks.isEmpty()) {
                                showState(showNotFound = true)
                            } else {
                                showState(showList = true)
                            }
                        }

                        override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                            searchAdapter.updateTracks(emptyList())
                            showState(showError = true)
                        }
                    })
                }

                true
            } else {
                false
            }
        }
    }

    private fun showState(
        showList: Boolean = false,
        showNotFound: Boolean = false,
        showError: Boolean = false
    ) {
        recyclerView.visibility = View.VISIBLE

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
}
