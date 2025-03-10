package com.example.spotifyclone.album.model;

import java.util.List;

public class Song {
    private String _id;
    private String title;
    private List<String> singer_ids; //array of singer name
    private List<String> author_ids;
    private List<String >genre_ids; //array of genre name
    private String lyrics;
    private Boolean is_premium;
    private int like_count;
    private String mp3_url;
    private String image_url;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getSinger_ids() {
        return singer_ids;
    }

    public void setSinger_ids(List<String> singer_ids) {
        this.singer_ids = singer_ids;
    }

    public List<String> getAuthor_ids() {
        return author_ids;
    }

    public void setAuthor_ids(List<String> author_ids) {
        this.author_ids = author_ids;
    }

    public List<String> getGenre_ids() {
        return genre_ids;
    }

    public void setGenre_ids(List<String> genre_ids) {
        this.genre_ids = genre_ids;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public Boolean getIs_premium() {
        return is_premium;
    }

    public void setIs_premium(Boolean is_premium) {
        this.is_premium = is_premium;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public String getMp3_url() {
        return mp3_url;
    }

    public void setMp3_url(String mp3_url) {
        this.mp3_url = mp3_url;
    }

    public String getImg_url() {
        return image_url;
    }

    public void setImg_url(String img_url) {
        this.image_url = img_url;
    }
}
