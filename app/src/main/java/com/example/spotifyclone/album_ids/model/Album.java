package com.example.spotifyclone.album_ids.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Album {
    @SerializedName("_id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("artist_ids")
    private List<String> artists_name;

    @SerializedName("release_date")
    private Date releaseDate;

    @SerializedName("cover_url")
    private String coverUrl;

    @SerializedName("song_ids")
    private List<String> songIds;

    @SerializedName("create_at")
    private Date createdAt;

    public Album() {
        // Constructor mặc định để Gson hoạt động
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getArtistIds() {
        return artists_name;
    }


    public void setArtistIds(List<String>artists_name) {
        this.artists_name = artists_name;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public List<String> getSongIds() {
        return songIds;
    }

    public void setSongIds(List<String> songIds) {
        this.songIds = songIds;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Album{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", artistIds=" + artists_name +
                ", releaseDate=" + releaseDate +
                ", coverUrl='" + coverUrl + '\'' +
                ", songIds=" + songIds +
                ", createdAt=" + createdAt +
                '}';
    }
}
