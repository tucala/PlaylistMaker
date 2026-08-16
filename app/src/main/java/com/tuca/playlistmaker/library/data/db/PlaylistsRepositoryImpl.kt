package com.tuca.playlistmaker.library.data.db

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.tuca.playlistmaker.library.data.db.converter.PlaylistDbConverter
import com.tuca.playlistmaker.library.domain.db.PlaylistsRepository
import com.tuca.playlistmaker.library.domain.models.Playlist
import com.tuca.playlistmaker.player.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PlaylistsRepositoryImpl(
    private val context: Context,
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter
) : PlaylistsRepository {

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        val entities = appDatabase.playlistDao().getAllPlaylists()
        emit(entities.map { playlistDbConverter.map(it) })
    }.flowOn(Dispatchers.IO)

    override suspend fun createPlaylist(
        name: String,
        description: String?,
        coverUri: String?
    ) {
        withContext(Dispatchers.IO) {
            val playlist = Playlist(
                id = 0,
                name = name,
                description = description,
                coverPath = coverUri,
                trackIds = emptyList(),
                tracksCount = 0
            )
            val entity = playlistDbConverter.map(playlist)
            appDatabase.playlistDao().insertPlaylist(entity)
        }
    }

    override suspend fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist
    ) {
        withContext(Dispatchers.IO) {
            val trackEntity = playlistDbConverter.map(track)
            appDatabase.playlistTrackDao().insertTrack(trackEntity)

            val updatedTrackIds = playlist.trackIds.toMutableList().apply {
                if (!contains(track.trackId)) {
                    add(track.trackId)
                }
            }
            val updatedPlaylist = playlist.copy(
                trackIds = updatedTrackIds,
                tracksCount = updatedTrackIds.size
            )
            val entity = playlistDbConverter.map(updatedPlaylist)
            appDatabase.playlistDao().updatePlaylist(entity)
        }
    }

    override suspend fun saveImageToPrivateStorage(uri: Uri): String = withContext(Dispatchers.IO) {
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlists")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        inputStream?.close()
        outputStream.close()
        file.absolutePath
    }
}
