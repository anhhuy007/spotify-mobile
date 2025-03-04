package com.example.spotifyclone.features.player.model.song;

import java.io.Serializable;

public class Song implements Serializable {
    private String _id;
    private String title;
    private String[] singer_ids;
    private String[] author_ids;
    private String[] genre_ids;
    private String lyrics;
    private boolean is_premium;
    private int like_count;
    private String mp3_url;
    private String image_url;

    // Constructor
    public Song(String _id, String title, String[] singer_ids, String[] author_ids, String[] genre_ids, String lyrics, boolean is_premium, int like_count, String mp3_url, String image_url) {
        this._id = _id;
        this.title = title;
        this.singer_ids = singer_ids;
        this.author_ids = author_ids;
        this.genre_ids = genre_ids;
        this.lyrics = lyrics;
        this.is_premium = is_premium;
        this.like_count = like_count;
        this.mp3_url = mp3_url;
        this.image_url = image_url;
    }

    // Getters
    public String get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String[] getSinger_ids() {
        return singer_ids;
    }

    public String[] getAuthor_ids() {
        return author_ids;
    }

    public String[] getGenre_ids() {
        return genre_ids;
    }

    public String getLyrics() {
        return lyrics;
    }

    public boolean isIs_premium() {
        return is_premium;
    }

    public int getLike_count() {
        return like_count;
    }

    public String getMp3_url() {
        return mp3_url;
    }

    public String getImage_url() {
        return image_url;
    }
}
