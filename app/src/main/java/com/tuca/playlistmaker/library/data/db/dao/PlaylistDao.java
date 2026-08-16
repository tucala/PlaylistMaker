package com.tuca.playlistmaker.library.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.tuca.playlistmaker.library.data.db.entity.PlaylistEntity;
import java.util.List;

@Dao
public interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPlaylist(PlaylistEntity playlist);

    @Update
    void updatePlaylist(PlaylistEntity playlist);

    @Query("SELECT * FROM playlists ORDER BY addedAt DESC")
    List<PlaylistEntity> getAllPlaylists();

    @Query("SELECT * FROM playlists WHERE id = :id")
    PlaylistEntity getPlaylistById(int id);
}
