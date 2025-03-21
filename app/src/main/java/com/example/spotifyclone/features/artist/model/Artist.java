package com.example.spotifyclone.features.artist.model;

public class Artist {
    private String _id;
    private String name;
    private String bio;
    private String avatar_url;
    private int followers;

    public String getId() { return _id; }
    public String getName() { return name; }
    public String getDescription() { return bio; }
    public String getAvatarUrl() { return avatar_url; }
    public  int getFollowers() {return followers; }
}
