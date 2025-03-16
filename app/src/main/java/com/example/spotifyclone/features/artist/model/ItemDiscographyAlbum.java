package com.example.spotifyclone.features.artist.model;

import java.util.Calendar;
import java.util.Date;

public class ItemDiscographyAlbum {
    private String _id;
    private String title;
    private Date create_at;
    private String cover_url;

    public ItemDiscographyAlbum() {
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getName() {
        return title;
    }

    public void setName(String name) {
        this.title = name;
    }

    public String getYear() {
        if (create_at == null) {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(create_at);
        return String.valueOf(cal.get(Calendar.YEAR));
    }

    public String getCoverUrl() {
        return cover_url;
    }

    public void setCoverUrl(String coverUrl) {
        this.cover_url = coverUrl;
    }

}

