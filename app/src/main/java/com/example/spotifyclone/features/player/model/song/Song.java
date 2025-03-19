package com.example.spotifyclone.features.player.model.song;

import com.example.spotifyclone.features.artist.model.Artist;
import com.example.spotifyclone.features.genre.model.Genre;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class Song implements Serializable {
    private String _id;
    private String title;
    private String lyric;
    private boolean is_premium;
    private int like_count;
    private String mp3_url;
    private String image_url;
    private List<Artist> singers;
    private List<Artist> authors;
    private List<Genre> genres;

    // Constructor
    public Song(String _id, String title, String lyrics, boolean is_premium, int like_count,
                String mp3_url, String image_url, List<Artist> authors, List<Artist> singers, List<Genre> genres) {
        this._id = _id;
        this.title = title;
        this.lyric = lyrics;
        this.is_premium = is_premium;
        this.like_count = like_count;
        this.mp3_url = mp3_url;
        this.image_url = image_url;
        this.singers = singers;
        this.authors = authors;
        this.genres = genres;
    }

    // Getters
    public String getId() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public String getLyrics() {
        return lyric;
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
    public List<String> getSingerNames() {
        return singers != null ? singers.stream().map(Artist::getName).collect(Collectors.toList()) : null;
    }

    public String getSingersString() {
        return singers != null ? singers.stream().map(Artist::getName).collect(Collectors.joining(", ")) : "Unknown";
    }

    public List<String> getAuthorNames() {
        return authors != null ? authors.stream().map(Artist::getName).collect(Collectors.toList()) : null;
    }

    public String getAuthorsString() {
        return authors != null ? authors.stream().map(Artist::getName).collect(Collectors.joining(", ")) : "Unknown";
    }

    public List<String> getGenreNames() {
        return genres != null ? genres.stream().map(Genre::getName).collect(Collectors.toList()) : null;
    }

    public String getGenresString() {
        return genres != null ? genres.stream().map(Genre::getName).collect(Collectors.joining(", ")) : "Unknown";
    }
    public String getSingerNameAt(int index) {
        return (singers != null && index >= 0 && index < singers.size()) ? singers.get(index).getName() : "Unknown";
    }

    public String getSingerImageUrlAt(int index) {
        return (singers != null && index >= 0 && index < singers.size()) ? singers.get(index).getAvatarUrl() : null;
    }

    public String getAuthorNameAt(int index) {
        return (authors != null && index >= 0 && index < authors.size()) ? authors.get(index).getName() : "Unknown";
    }

    public String getAuthorImageUrlAt(int index) {
        return (authors != null && index >= 0 && index < authors.size()) ? authors.get(index).getAvatarUrl() : null;
    }

    public String getAuthorBioAt(int index) {
        return (authors != null && index >= 0 && index < authors.size()) ? authors.get(index).getDescription() : null;
    }

    public int getSingerFollowersAt(int index) {
        return (authors != null && index >= 0 && index < authors.size()) ? authors.get(index).getFollowers() : null;
    }

    public int getAuthorFollowersAt(int index) {
        return (authors != null && index >= 0 && index < authors.size()) ? authors.get(index).getFollowers() : null;
    }

    public String getSingerBioAt(int index) {
        return (authors != null && index >= 0 && index < authors.size()) ? authors.get(index).getDescription() : null;
    }


    // Setters
    public void setId(String _id) {
        this._id = _id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLyrics(String lyrics) {
        this.lyric = lyrics;
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
}
