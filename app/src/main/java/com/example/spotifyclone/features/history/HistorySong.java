package com.example.spotifyclone.features.history;

public class HistorySong {
    private String songId;
    private long date;

    // Constructor
    public HistorySong(String songId, long date) {
        this.songId = songId;
        this.date = date;
    }
    public HistorySong() {
        this.songId = "";
        this.date = 0;
    }

    // Getters
    public String getSongId() {
        return songId;
    }
    public long getDate() {
        return date;
    }
    // Setters
    public void setSongId(String songId) {
        this.songId = songId;
    }
    public void setDate(long date) {
        this.date = date;
    }
}
