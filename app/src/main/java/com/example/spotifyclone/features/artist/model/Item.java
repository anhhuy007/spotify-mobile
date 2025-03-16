package com.example.spotifyclone.features.artist.model;

public class Item {
    private String _id;
    private String name;
    private String bio;
    private String avatar_url;
    private String createdAt;

    public String getId() { return _id; }
    public String getName() { return name; }
    public String getDescription() { return bio; }
    public String getAvatarUrl() { return avatar_url; }
    public String getCreatedAt() { return createdAt; }
}
