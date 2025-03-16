package com.example.spotifyclone.features.artist.model;

import java.util.Calendar;
import java.util.Date;

public class ItemDiscographyEP {

    private String _id;
    private String title;
    private Date create_at;
    private String image_url;


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
        return image_url;
    }

    public void setCoverUrl(String coverUrl) {
        this.image_url = coverUrl;
    }
}
