package com.example.spotifyclone.features.genre.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Album {
    @SerializedName("_id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("artist")
    private List<String> artists_name;

    @SerializedName("release_date")
    private Date releaseDate;

    @SerializedName("cover_url")
    private String coverUrl;

//    @SerializedName("song_ids")
//    private List<String> songIds;

    @SerializedName("create_at")
    private Date createdAt;

    @SerializedName("like_count")
    private int like_count;

    @SerializedName("updatedAt")
    private Date updatedAt;

    @SerializedName("artist_url")
    private List<String> artist_url;


    public List<String> getArtist_url() {
        return artist_url;
    }

    public void setArtist_url(List<String> artist_url) {
        this.artist_url = artist_url;
    }

    public List<String> getArtists_name() {
        return artists_name;
    }

    public void setArtists_name(List<String> artists_name) {
        this.artists_name = artists_name;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

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
                ", createdAt=" + createdAt +
                '}';
    }
}