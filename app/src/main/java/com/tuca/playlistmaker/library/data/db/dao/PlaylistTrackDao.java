package com.tuca.playlistmaker.library.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.tuca.playlistmaker.library.data.db.entity.PlaylistTrackEntity;
import java.util.List;

@Dao
public interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTrack(PlaylistTrackEntity track);

    @Query("SELECT * FROM playlist_tracks WHERE trackId = :trackId")
    PlaylistTrackEntity getTrackById(long trackId);

    @Query("SELECT * FROM playlist_tracks")
    List<PlaylistTrackEntity> getAllTracks();

    @Query("DELETE FROM playlist_tracks WHERE trackId = :trackId")
    void deleteTrackById(long trackId);
}
