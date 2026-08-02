package com.tuca.playlistmaker.library.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.tuca.playlistmaker.library.data.db.entity.TrackEntity;
import java.util.List;

@Dao
public interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrack(TrackEntity track);

    @Delete
    void deleteTrack(TrackEntity track);

    @Query("SELECT * FROM favorite_tracks ORDER BY addedAt DESC")
    List<TrackEntity> getFavoriteTracks();

    @Query("SELECT trackId FROM favorite_tracks")
    List<Integer> getFavoriteTrackIds();
}
