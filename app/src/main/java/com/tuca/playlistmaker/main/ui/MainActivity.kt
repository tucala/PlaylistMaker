package com.tuca.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tuca.playlistmaker.R
import com.tuca.playlistmaker.library.ui.LibraryActivity
import com.tuca.playlistmaker.search.ui.SearchActivity
import com.tuca.playlistmaker.settings.ui.SettingsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val searchButton: Button = findViewById(R.id.search)
        val libraryButton: Button = findViewById(R.id.library)
        val settingsButton: Button = findViewById(R.id.settings)

        searchButton.setOnClickListener { viewModel.onSearchClicked() }
        libraryButton.setOnClickListener { viewModel.onLibraryClicked() }
        settingsButton.setOnClickListener { viewModel.onSettingsClicked() }

        viewModel.state.observe(this) { state ->
            render(state)
        }
    }

    private fun render(state: MainState) {
        when (state) {
            is MainState.Content -> {
            }
            is MainState.NavigateToSearch -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                viewModel.onNavigationHandled() // Уведомляем ViewModel, что переход обработан
            }
            is MainState.NavigateToLibrary -> {
                val intent = Intent(this, LibraryActivity::class.java)
                startActivity(intent)
                viewModel.onNavigationHandled()
            }
            is MainState.NavigateToSettings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                viewModel.onNavigationHandled()
            }
        }
    }
}
