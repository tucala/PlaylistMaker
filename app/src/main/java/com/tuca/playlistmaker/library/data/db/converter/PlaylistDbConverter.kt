package com.tuca.playlistmaker.library.data.db.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tuca.playlistmaker.library.data.db.entity.PlaylistEntity
import com.tuca.playlistmaker.library.data.db.entity.PlaylistTrackEntity
import com.tuca.playlistmaker.library.domain.models.Playlist
import com.tuca.playlistmaker.player.domain.models.Track

class PlaylistDbConverter(private val gson: Gson = Gson()) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.coverPath,
            gson.toJson(playlist.trackIds),
            playlist.tracksCount,
            System.currentTimeMillis()
        )
    }

    fun map(entity: PlaylistEntity): Playlist {
        val listType = object : TypeToken<List<Long>>() {}.type
        val trackIds: List<Long> = if (entity.trackIdsJson.isNullOrEmpty()) {
            emptyList()
        } else {
            gson.fromJson(entity.trackIdsJson, listType) ?: emptyList()
        }
        return Playlist(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            coverPath = entity.coverPath,
            trackIds = trackIds,
            tracksCount = entity.tracksCount
        )
    }

    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.previewUrl,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            System.currentTimeMillis()
        )
    }

    fun map(entity: PlaylistTrackEntity): Track {
        return Track(
            trackId = entity.trackId,
            trackName = entity.trackName ?: "",
            artistName = entity.artistName ?: "",
            previewUrl = entity.previewUrl,
            trackTimeMillis = entity.trackTimeMillis,
            artworkUrl100 = entity.artworkUrl100 ?: "",
            collectionName = entity.collectionName,
            releaseDate = entity.releaseDate,
            primaryGenreName = entity.primaryGenreName,
            country = entity.country,
            isFavorite = false
        )
    }
}
