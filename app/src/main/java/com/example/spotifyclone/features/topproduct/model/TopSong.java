package com.example.spotifyclone.features.topproduct.model;

import java.util.List;

public class TopSong {
    private String _id;
    private String title;
    private List<String> artist_name;
    private String image_url;

    public String getId() { return _id; }
    public String getName() { return title; }
    public String getDescription() {
        return String.join(", ", artist_name);
    }
    public String getAvatarUrl() { return image_url; }
}
