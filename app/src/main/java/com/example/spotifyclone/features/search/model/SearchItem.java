package com.example.spotifyclone.features.search.model;

import java.util.List;

public class SearchItem {
    private String _id;
    private String name;
    private String image_url;
    private String type;
    private List<String> artists_name; //only when return is album/song

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getArtists_name() {
        return artists_name;
    }

    public void setArtists_name(List<String> artists_name) {
        this.artists_name = artists_name;
    }
}
