package com.example.spotifyclone.features.library.model;

public class SelectableArtist {
    private String _id;
    private String name;
    private String image_url;
    private boolean isFollow;

    public SelectableArtist() {
    }

    public SelectableArtist(String id, String name, String imageUrl, boolean isFollowed) {
        this._id = id;
        this.name = name;
        this.image_url = imageUrl;
        this.isFollow = isFollowed;
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

    public boolean isFollowed() {
        return isFollow;
    }

    public void setFollowed(boolean followed) {
        isFollow = followed;
    }
}