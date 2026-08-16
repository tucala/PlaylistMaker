package com.tuca.playlistmaker.library.data.db.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "playlists")
public class PlaylistEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String description;
    public String coverPath;
    public String trackIdsJson;
    public int tracksCount;
    public long addedAt;

    public PlaylistEntity() {}

    @Ignore
    public PlaylistEntity(
            int id,
            String name,
            String description,
            String coverPath,
            String trackIdsJson,
            int tracksCount,
            long addedAt
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.coverPath = coverPath;
        this.trackIdsJson = trackIdsJson;
        this.tracksCount = tracksCount;
        this.addedAt = addedAt;
    }
}
