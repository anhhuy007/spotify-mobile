package com.example.spotifyclone.features.player.model.song;

import com.example.spotifyclone.features.artist.model.Artist;

import java.io.Serializable;
import java.util.List;

public class SongDetail implements Serializable {
    private String _id;
    private String title;
    private String lyrics;
    private boolean is_premium;
    private int like_count;
    private String mp3_url;
    private String image_url;
    private String create_at;
    private List<Artist> artists;

    // Constructor
    public SongDetail (String _id, String title, String lyrics, boolean is_premium, int like_count,
                String mp3_url, String image_url, String create_at, List<Artist> artists) {
        this._id = _id;
        this.title = title;
        this.lyrics = lyrics;
        this.is_premium = is_premium;
        this.like_count = like_count;
        this.mp3_url = mp3_url;
        this.image_url = image_url;
        this.create_at = create_at;
        this.artists = artists;
    }

    // Getters
    public String getId() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getLyrics() {
        return lyrics;
    }

    public boolean isPremium() {
        return is_premium;
    }

    public int getLikeCount() {
        return like_count;
    }

    public String getMp3Url() {
        return mp3_url;
    }

    public String getImageUrl() {
        return image_url;
    }

    public String getCreateAt() {
        return create_at;
    }

    // Setters
    public void setId(String _id) {
        this._id = _id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public void setPremium(boolean is_premium) {
        this.is_premium = is_premium;
    }

    public void setLikeCount(int like_count) {
        this.like_count = like_count;
    }

    public void setMp3Url(String mp3_url) {
        this.mp3_url = mp3_url;
    }

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }

    public void setCreateAt(String create_at) {
        this.create_at = create_at;
    }

}
