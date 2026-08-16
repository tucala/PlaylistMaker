package com.tuca.playlistmaker.library.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_tracks")
public class TrackEntity {
    @PrimaryKey
    public int trackId;
    public String trackName;
    public String artistName;
    public String previewUrl;
    public long trackTimeMillis;
    public String artworkUrl100;
    public String collectionName;
    public String releaseDate;
    public String primaryGenreName;
    public String country;
    public long addedAt;

    public TrackEntity() {}

    public TrackEntity(
            int trackId,
            String trackName,
            String artistName,
            String previewUrl,
            long trackTimeMillis,
            String artworkUrl100,
            String collectionName,
            String releaseDate,
            String primaryGenreName,
            String country,
            long addedAt
    ) {
        this.trackId = trackId;
        this.trackName = trackName;
        this.artistName = artistName;
        this.previewUrl = previewUrl;
        this.trackTimeMillis = trackTimeMillis;
        this.artworkUrl100 = artworkUrl100;
        this.collectionName = collectionName;
        this.releaseDate = releaseDate;
        this.primaryGenreName = primaryGenreName;
        this.country = country;
        this.addedAt = addedAt;
    }
}
