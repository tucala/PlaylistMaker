package com.tuca.playlistmaker

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackName: String,
    val artistName: String,
    @SerializedName("previewUrl")
    val previewUrl: String?,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String? = null,
    val releaseDate: String? = null,
    val primaryGenreName: String? = null,
    val country: String? = null
) : Serializable {
    val trackTime: String
        get() = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(trackTimeMillis)

    fun getCoverArtwork(): String = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}
