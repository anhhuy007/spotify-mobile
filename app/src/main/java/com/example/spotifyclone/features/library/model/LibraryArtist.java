package com.example.spotifyclone.features.library.model;

public class LibraryArtist {
    private String _id;
    private String name;
    private String image_url;
    private Integer flCount;

    public LibraryArtist() {
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
        return image_url;
    }

    public void setImageUrl(String imageUrl) {
        this.image_url = imageUrl;
    }

    public String getFlCount() {
        return Integer.toString(flCount);
    }

    public void setFlCount(Integer type) {
        this.flCount = type;
    }
}