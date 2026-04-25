package com.tuca.playlistmaker.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.tuca.playlistmaker.domain.api.HistoryRepository
import com.tuca.playlistmaker.domain.models.Track

class HistoryRepositoryImpl(
    private val prefs: SharedPreferences
) : HistoryRepository {
    companion object {
        private const val KEY_HISTORY = "search_history"
        private const val MAX_HISTORY = 10
    }

    override fun addTrack(track: Track) {
        val current = getHistory().toMutableList()

        current.removeAll { it.trackName == track.trackName && it.artistName == track.artistName }
        current.add(0, track)
        if (current.size > MAX_HISTORY) {
            current.subList(MAX_HISTORY, current.size).clear()
        }
        saveHistory(current)
    }

    override fun getHistory(): List<Track> {
        val saved = prefs.getString(KEY_HISTORY, "") ?: ""
        if (saved.isEmpty()) return emptyList()

        val list = mutableListOf<Track>()
        val items = saved.split(";")
        for (item in items) {
            val parts = item.split("|")
            if (parts.size >= 4) {
                list.add(
                    Track(
                        trackName = parts[0],
                        artistName = parts[1],
                        trackTimeMillis = parts[2].toLongOrNull() ?: 0L,
                        artworkUrl100 = parts[3],
                        collectionName = parts.getOrNull(4).orEmpty().ifBlank { null },
                        releaseDate = parts.getOrNull(5).orEmpty().ifBlank { null },
                        primaryGenreName = parts.getOrNull(6).orEmpty().ifBlank { null },
                        country = parts.getOrNull(7).orEmpty().ifBlank { null },
                        previewUrl = parts.getOrNull(8).orEmpty().ifBlank { null }
                    )
                )
            }
        }
        return list
    }

    override fun clearHistory() {
        prefs.edit { remove(KEY_HISTORY) }
    }

    private fun saveHistory(list: List<Track>) {
        val builder = StringBuilder()
        list.forEachIndexed { index, track ->
            builder.append(
                "${track.trackName}|${track.artistName}|${track.trackTimeMillis}|${track.artworkUrl100}" +
                    "|${track.collectionName.orEmpty()}|${track.releaseDate.orEmpty()}" +
                    "|${track.primaryGenreName.orEmpty()}|${track.country.orEmpty()}|${track.previewUrl.orEmpty()}"
            )
            if (index != list.size - 1) builder.append(";")
        }
        prefs.edit { putString(KEY_HISTORY, builder.toString()) }
    }
}
