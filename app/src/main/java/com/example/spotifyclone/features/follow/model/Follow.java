package com.example.spotifyclone.features.follow.model;

public class Follow {
    private String _id;
    private String user_id;
    private String artist_id;

    public Follow() {
    }

    public Follow(String id, String userId, String artistId) {
        this._id = id;
        this.user_id = userId;
        this.artist_id = artistId;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String userId) {
        this.user_id = userId;
    }

    public String getArtistId() {
        return artist_id;
    }

    public void setArtistId(String artistId) {
        this.artist_id = artistId;
    }
}
