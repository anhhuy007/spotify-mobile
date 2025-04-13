package com.example.spotifyclone.features.topproduct.model;

import java.util.List;

public class AlsoLike {
    private String _id;
    private String title;
    private List<String> artist_name;
    private String cover_url;

    public String getId() { return _id; }
    public String getName() { return title; }
    public String getDescription() {
        return String.join(", ", artist_name);
    }
    public String getAvatarUrl() { return cover_url; }
}