package com.tuca.playlistmaker.library.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.tuca.playlistmaker.library.data.db.dao.PlaylistDao;
import com.tuca.playlistmaker.library.data.db.dao.PlaylistTrackDao;
import com.tuca.playlistmaker.library.data.db.dao.TrackDao;
import com.tuca.playlistmaker.library.data.db.entity.PlaylistEntity;
import com.tuca.playlistmaker.library.data.db.entity.PlaylistTrackEntity;
import com.tuca.playlistmaker.library.data.db.entity.TrackEntity;

@Database(
    entities = {TrackEntity.class, PlaylistEntity.class, PlaylistTrackEntity.class},
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TrackDao trackDao();
    public abstract PlaylistDao playlistDao();
    public abstract PlaylistTrackDao playlistTrackDao();
}
