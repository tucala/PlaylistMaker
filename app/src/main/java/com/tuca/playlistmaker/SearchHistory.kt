package com.tuca.playlistmaker

import android.content.SharedPreferences

class SearchHistory(private val prefs: SharedPreferences) {

    companion object {
        private const val KEY_HISTORY = "search_history"
        private const val MAX_HISTORY = 10
    }

    fun getHistory(): List<SearchActivity.Track> {
        val saved = prefs.getString(KEY_HISTORY, "") ?: ""
        if (saved.isEmpty()) return emptyList()

        val list = mutableListOf<SearchActivity.Track>()
        val items = saved.split(";")
        for (item in items) {
            val parts = item.split("|")
            if (parts.size == 4) {
                val track = SearchActivity.Track(
                    trackName = parts[0],
                    artistName = parts[1],
                    trackTimeMillis = parts[2].toIntOrNull() ?: 0,
                    artworkUrl100 = parts[3]
                )
                list.add(track)
            }
        }
        return list
    }

    fun addTrack(track: SearchActivity.Track) {
        val current = getHistory().toMutableList()

        current.removeAll { it.trackName == track.trackName && it.artistName == track.artistName }
        current.add(0, track)
        if (current.size > MAX_HISTORY) {
            current.subList(MAX_HISTORY, current.size).clear()
        }
        saveHistory(current)
    }

    fun clear() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    private fun saveHistory(list: List<SearchActivity.Track>) {
        val builder = StringBuilder()
        list.forEachIndexed { index, track ->
            builder.append("${track.trackName}|${track.artistName}|${track.trackTimeMillis}|${track.artworkUrl100}")
            if (index != list.size - 1) builder.append(";")
        }
        prefs.edit().putString(KEY_HISTORY, builder.toString()).apply()
    }
}