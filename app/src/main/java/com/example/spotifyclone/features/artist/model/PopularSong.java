package com.example.spotifyclone.features.artist.model;

public class PopularSong {

    private String _id;
    private String title;
    private String like_count;
    private String image_url;


    public String getId() { return _id; }
    public String getName() { return title; }
    public String getDescription() { return like_count; }
    public String getAvatarUrl() { return image_url; }
}
