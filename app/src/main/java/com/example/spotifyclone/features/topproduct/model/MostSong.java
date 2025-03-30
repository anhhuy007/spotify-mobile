package com.example.spotifyclone.features.topproduct.model;

import java.util.List;

public class MostSong {
    private String _id;
    private String title;
    private String play_count;
    private String image_url;

    public String getId() { return _id; }
    public String getName() { return title; }
    public String getDescription() {
        return play_count;
    }
    public String getAvatarUrl() { return image_url; }
}
