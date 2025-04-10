package com.example.spotifyclone.features.library.model;

public class LibraryPlaylist {
    private String _id;
    private String name;
    private String cover_url;
    private String creator_name;
    private int song_count;

    public LibraryPlaylist() {
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return cover_url;
    }

    public void setImageUrl(String imageUrl) {
        this.cover_url = imageUrl;
    }

    public String getCreatorName() {
        return creator_name;
    }

    public void setCreatorName(String creatorName) {
        this.creator_name = creatorName;
    }

    public int getSongCount() {
        return song_count;
    }

    public void setSongCount(int songCount) {
        this.song_count = songCount;
    }

    public String getPlaylistInfo() {
        return creator_name;
    }
}